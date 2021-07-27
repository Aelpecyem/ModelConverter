package de.aelpecyem.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelPartData {
    public boolean isTop = true;
    private final String name;
    private final ModelTransform transform = new ModelTransform();
    private final ModelPartBuilder builder = new ModelPartBuilder();
    private final List<ModelPartData> children = new ArrayList<>();

    public ModelPartData(String name){
        this.name = name;
    }

    public ModelPartBuilder getBuilder() {
        return builder;
    }

    public ModelTransform getTransform() {
        return transform;
    }

    public String getName() {
        return name;
    }

    public void addChild(ModelPartData data){
        this.children.add(data);
        data.isTop = false;
    }

    public void writeString(StringBuilder builder, String directParent){
        if (!children.isEmpty()){ //define variable so children can consistently be appended
            builder.append("ModelPartData %s = ".formatted(name));
        }
        builder.append("%s.addChild(\"%s\",".formatted(directParent, name));
        this.builder.writeString(builder);
        builder.append(',');
        this.transform.writeString(builder);
        builder.append(");\n");
        for (ModelPartData child : children) {
            child.writeString(builder, name);
        }
    }


    public void writeAsExternal(StringBuilder builder, String directParent){
        builder.append("ModelPartData %s = %s.get(\"%s\");\n".formatted(name, directParent, name));
    }

    public static final class ModelPartBuilder {
        private final int[] uv = new int[2];
        private boolean mirrored = false;
        private final List<CubeData> cuboids = new ArrayList<>();


        public void writeString(StringBuilder builder) {
            builder.append("\n\tModelPartBuilder.create()");
            int[] lastUV = {0, 0};
            boolean lastMirrored = false;
            for (CubeData cuboid : cuboids) {
                builder.append("\n\t\t");
                if (!Arrays.equals(lastUV, cuboid.uv)){
                   builder.append(".uv(%d, %d)".formatted(cuboid.uv[0], cuboid.uv[1]));
                   lastUV = cuboid.uv;
                }
                if (lastMirrored != cuboid.mirrored){
                    builder.append(".mirrored(%b)".formatted(cuboid.mirrored));
                    lastMirrored = cuboid.mirrored;
                }
                boolean needsDilation = !(cuboid.dimensions[6] == 0 && cuboid.dimensions[7] == 0 && cuboid.dimensions[8] == 0);
                builder.append(".cuboid(%sF, %sF, %sF, %sF, %sF, %sF%s)".formatted(cuboid.dimensions[0], cuboid.dimensions[1], cuboid.dimensions[2],
                        cuboid.dimensions[3], cuboid.dimensions[4], cuboid.dimensions[5],
                        needsDilation ? ", new Dilation(%sF, %sF, %sF)".formatted(cuboid.dimensions[6], cuboid.dimensions[7], cuboid.dimensions[8]): ""));
            }
        }

        public ModelPartBuilder setUV(int u, int v) {
            this.uv[0] = u;
            this.uv[1] = v;
            return this;
        }

        public ModelPartBuilder setMirrored(boolean mirrored) {
            this.mirrored = mirrored;
            return this;
        }

        public ModelPartBuilder addCuboid(float x, float y, float z, float width, float height, float depth, float dilX, float dilY, float dilZ) {
            cuboids.add(new CubeData(new int[]{uv[0], uv[1]}, new float[]{x, y, z, width, height, depth, dilX, dilY, dilZ}, mirrored));
            return this;
        }

        private record CubeData(int[] uv, float[] dimensions, boolean mirrored){}
    }

    public static final class ModelTransform {
        private float[] pivot = new float[3];
        private float[] rotation = new float[3];


        public void writeString(StringBuilder builder) {
            builder.append(String.format("\n\tModelTransform.of(%sF, %sF, %sF, %sF, %sF, %sF)", pivot[0], pivot[1], pivot[2], rotation[0], rotation[1], rotation[2]));
        }

        public ModelTransform setPivot(float pivotX, float pivotY, float pivotZ) {
            this.pivot[0] = pivotX;
            this.pivot[1] = pivotY;
            this.pivot[2] = pivotZ;
            return this;
        }

        public ModelTransform setRotation(float rotX, float rotY, float rotZ) {
            this.rotation[0] = rotX;
            this.rotation[1] = rotY;
            this.rotation[2] = rotZ;
            return this;
        }
    }
}
