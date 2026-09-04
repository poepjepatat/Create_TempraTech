#!/usr/bin/env python3

import argparse
import json
import re
import sys
from pathlib import Path


# ============================================================
# DEFAULT CONFIGURATION
# ============================================================

DEFAULT_MOD_ID = "create_tempratech"

DEFAULT_BLOCKSTATE_DIR = (
    "src/main/resources/assets/create_tempratech/blockstates"
)

DEFAULT_MODEL_DIR = (
    "src/main/resources/assets/create_tempratech/models"
)

DEFAULT_OUTPUT_DIR = (
    "src/main/java/Mods/create_tempratech/Regs/Blocks/generated"
)

DEFAULT_PACKAGE = (
    "Mods.create_tempratech.Regs.Blocks.generated"
)


# ============================================================
# CONNECTION BIT VALUES
# ============================================================

CONNECTION_BITS = {
    "up": 1,
    "down": 2,
    "north": 4,
    "south": 8,
    "east": 16,
    "west": 32,
}


# ============================================================
# JAVA NAMING
# ============================================================

def to_pascal_case(name):
    parts = re.split(r"[^a-zA-Z0-9]+", name)

    return "".join(
        part[:1].upper() + part[1:]
        for part in parts
        if part
    )


# ============================================================
# NUMBER FORMAT
# ============================================================

def format_number(value):

    value = float(value)

    if value == 0:
        return "0"

    if value.is_integer():
        return str(int(value))

    return f"{value:.6f}".rstrip("0").rstrip(".")


# ============================================================
# READ JSON
# ============================================================

def read_json(path):

    try:

        with path.open(
            "r",
            encoding="utf-8"
        ) as file:

            return json.load(file)

    except FileNotFoundError:

        raise RuntimeError(
            f"File not found: {path}"
        )

    except json.JSONDecodeError as exc:

        raise RuntimeError(
            f"Invalid JSON in {path}: {exc}"
        )


# ============================================================
# MODEL PATH
# ============================================================

def model_resource_to_path(
    model_resource,
    model_dir
):
    """
    Converts:

    create_tempratech:block/heat_pipe/heat_pipe_core

    into:

    models/block/heat_pipe/heat_pipe_core.json
    """

    if ":" in model_resource:

        namespace, path = model_resource.split(
            ":",
            1
        )

    else:

        namespace = DEFAULT_MOD_ID
        path = model_resource

    if namespace != DEFAULT_MOD_ID:

        raise RuntimeError(
            f"Model belongs to namespace "
            f"'{namespace}', expected "
            f"'{DEFAULT_MOD_ID}'."
        )

    return model_dir / f"{path}.json"


# ============================================================
# MODEL ELEMENT → VOXEL BOX
# ============================================================

def element_to_box(
    element,
    model_path,
    index
):

    if "from" not in element:

        raise RuntimeError(
            f"{model_path}: element {index} "
            f"is missing 'from'."
        )

    if "to" not in element:

        raise RuntimeError(
            f"{model_path}: element {index} "
            f"is missing 'to'."
        )

    from_pos = element["from"]
    to_pos = element["to"]

    if len(from_pos) != 3 or len(to_pos) != 3:

        raise RuntimeError(
            f"{model_path}: element {index} "
            f"must have 3 coordinates."
        )

    x1, y1, z1 = from_pos
    x2, y2, z2 = to_pos

    # --------------------------------------------------------
    # Rotation warning
    # --------------------------------------------------------

    rotation = element.get("rotation")

    if rotation:

        angle = rotation.get(
            "angle",
            0
        )

        if angle != 0:

            print(
                f"WARNING: {model_path}: "
                f"element {index} has rotation "
                f"(angle={angle})."
            )

            print(
                "         The collision shape will "
                "use its axis-aligned bounding box."
            )

    # --------------------------------------------------------
    # Normalize coordinates
    # --------------------------------------------------------

    x1 = min(float(x1), float(x2))
    x2 = max(float(x1), float(x2))

    y1 = min(float(y1), float(y2))
    y2 = max(float(y1), float(y2))

    z1 = min(float(z1), float(z2))
    z2 = max(float(z1), float(z2))

    return (
        f"Block.box("
        f"{format_number(x1)}, "
        f"{format_number(y1)}, "
        f"{format_number(z1)}, "
        f"{format_number(x2)}, "
        f"{format_number(y2)}, "
        f"{format_number(z2)}"
        f")"
    )


# ============================================================
# MODEL → LIST OF BOXES
# ============================================================

def read_model_boxes(
    model_resource,
    model_dir
):

    model_path = model_resource_to_path(
        model_resource,
        model_dir
    )

    data = read_json(model_path)

    elements = data.get(
        "elements",
        []
    )

    boxes = []

    for index, element in enumerate(elements):

        boxes.append(
            element_to_box(
                element,
                model_path,
                index
            )
        )

    if not boxes:

        raise RuntimeError(
            f"Model has no elements: {model_path}"
        )

    return boxes


# ============================================================
# PARSE HEAT PIPE BLOCKSTATE
# ============================================================

def parse_multipart_blockstate(
    blockstate_path
):

    data = read_json(
        blockstate_path
    )

    if "multipart" not in data:

        raise RuntimeError(
            f"{blockstate_path} does not contain "
            f"'multipart'."
        )

    multipart = data["multipart"]

    models = {}

    for part in multipart:

        # ----------------------------------------------------
        # Always-applied model
        # ----------------------------------------------------

        if "when" not in part:

            apply = part.get(
                "apply"
            )

            if not apply:

                continue

            model = apply.get(
                "model"
            )

            if model:

                models["core"] = model

            continue

        # ----------------------------------------------------
        # Conditional model
        # ----------------------------------------------------

        when = part["when"]

        apply = part.get(
            "apply"
        )

        if not apply:

            continue

        model = apply.get(
            "model"
        )

        if not model:

            continue

        # ----------------------------------------------------
        # Single-property condition
        # ----------------------------------------------------

        for direction in CONNECTION_BITS:

            if direction in when:

                value = when[direction]

                if value is True:

                    models[direction] = model

                elif value == "true":

                    models[direction] = model

    return models


# ============================================================
# BUILD SHAPE EXPRESSION
# ============================================================

def combine_boxes(boxes):

    if not boxes:

        return "Shapes.empty()"

    if len(boxes) == 1:

        return boxes[0]

    return (
        "Shapes.or(\n"
        + ",\n".join(
            f"            {box}"
            for box in boxes
        )
        + "\n        )"
    )


# ============================================================
# GENERATE 64 SHAPES
# ============================================================

def generate_shape_array(
    model_boxes
):

    output = []

    output.append(
        "    private static final VoxelShape[] SHAPES = new VoxelShape[64];"
    )

    output.append("")

    output.append(
        "    static {"
    )

    output.append("")

    # --------------------------------------------------------
    # Generate every combination
    # --------------------------------------------------------

    for mask in range(64):

        boxes = []

        # Core is always present
        boxes.extend(
            model_boxes["core"]
        )

        # Add connection models
        for direction, bit in CONNECTION_BITS.items():

            if mask & bit:

                boxes.extend(
                    model_boxes[direction]
                )

        shape = combine_boxes(
            boxes
        )

        output.append(
            f"        SHAPES[{mask}] = {shape};"
        )

        output.append("")

    output.append(
        "    }"
    )

    return "\n".join(output)


# ============================================================
# GENERATE JAVA CLASS
# ============================================================

def generate_java(
    block_name,
    model_boxes,
    package_name
):

    class_name = (
        to_pascal_case(block_name)
        + "Shapes"
    )

    shape_array = generate_shape_array(
        model_boxes
    )

    java = f"""package {package_name};

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;

/**
 * Automatically generated collision shapes for:
 * {block_name}
 *
 * DO NOT EDIT THIS FILE MANUALLY.
 *
 * Generated by generate_shapes.py
 */
public final class {class_name} {{

    private {class_name}() {{
    }}

{shape_array}

    /**
     * Gets the collision shape for the current
     * pipe connection state.
     *
     * Bit layout:
     *
     * UP    = 1
     * DOWN  = 2
     * NORTH = 4
     * SOUTH = 8
     * EAST  = 16
     * WEST  = 32
     */
    public static VoxelShape getShape(
            BlockState state
    ) {{

        int mask = 0;

        if (state.getValue(HeatPipe.UP)) {{
            mask |= 1;
        }}

        if (state.getValue(HeatPipe.DOWN)) {{
            mask |= 2;
        }}

        if (state.getValue(HeatPipe.NORTH)) {{
            mask |= 4;
        }}

        if (state.getValue(HeatPipe.SOUTH)) {{
            mask |= 8;
        }}

        if (state.getValue(HeatPipe.EAST)) {{
            mask |= 16;
        }}

        if (state.getValue(HeatPipe.WEST)) {{
            mask |= 32;
        }}

        return SHAPES[mask];
    }}
}}
"""

    return java


# ============================================================
# MAIN HEAT PIPE GENERATOR
# ============================================================

def generate_heat_pipe(
    blockstate_path,
    model_dir,
    output_dir,
    package_name
):

    print()
    print("==============================================")
    print("Generating Heat Pipe collision shapes")
    print("==============================================")

    print(
        f"Blockstate: {blockstate_path}"
    )

    # --------------------------------------------------------
    # Parse blockstate
    # --------------------------------------------------------

    models = parse_multipart_blockstate(
        blockstate_path
    )

    print()
    print("Models found:")

    for name, model in models.items():

        print(
            f"  {name:>6} -> {model}"
        )

    # --------------------------------------------------------
    # Make sure all six directions exist
    # --------------------------------------------------------

    required = [
        "core",
        "up",
        "down",
        "north",
        "south",
        "east",
        "west",
    ]

    missing = [
        name
        for name in required
        if name not in models
    ]

    if missing:

        raise RuntimeError(
            "Missing models in blockstate: "
            + ", ".join(missing)
        )

    # --------------------------------------------------------
    # Read geometry from every model
    # --------------------------------------------------------

    model_boxes = {}

    for name, model_resource in models.items():

        print()
        print(
            f"Reading {name} model..."
        )

        boxes = read_model_boxes(
            model_resource,
            model_dir
        )

        model_boxes[name] = boxes

        print(
            f"  Found {len(boxes)} box(es)"
        )

    # --------------------------------------------------------
    # Generate Java
    # --------------------------------------------------------

    java = generate_java(
        block_name="heat_pipe",
        model_boxes=model_boxes,
        package_name=package_name
    )

    # --------------------------------------------------------
    # Output
    # --------------------------------------------------------

    output_dir.mkdir(
        parents=True,
        exist_ok=True
    )

    output_file = (
        output_dir
        / "HeatPipeShapes.java"
    )

    with output_file.open(
        "w",
        encoding="utf-8"
    ) as file:

        file.write(java)

    print()
    print(
        f"Generated: {output_file}"
    )

    print()
    print(
        "Generated 64 connection combinations."
    )


# ============================================================
# COMMAND LINE
# ============================================================

def main():

    parser = argparse.ArgumentParser(
        description=(
            "Generate Minecraft VoxelShapes "
            "from a multipart blockstate."
        )
    )

    parser.add_argument(
        "--blockstate",
        default=(
            f"{DEFAULT_BLOCKSTATE_DIR}/heat_pipe.json"
        )
    )

    parser.add_argument(
        "--models",
        default=DEFAULT_MODEL_DIR
    )

    parser.add_argument(
        "--output",
        default=DEFAULT_OUTPUT_DIR
    )

    parser.add_argument(
        "--package",
        default=DEFAULT_PACKAGE
    )

    args = parser.parse_args()

    try:

        generate_heat_pipe(
            blockstate_path=Path(
                args.blockstate
            ),
            model_dir=Path(
                args.models
            ),
            output_dir=Path(
                args.output
            ),
            package_name=args.package
        )

    except Exception as exc:

        print()
        print(
            f"ERROR: {exc}"
        )

        return 1

    return 0


if __name__ == "__main__":
    sys.exit(main())