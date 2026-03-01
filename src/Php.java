import java.util.List;

interface PhpType {}

interface DocTag {
    String getValue();
}

interface PhpDocBlock {
    List<DocTag> getTagsByName(String tagName);
}

interface PhpVariable {
    PhpDocBlock getDocBlock();
    String getName();
}

class TypeFactory {
    public static PhpType createType(String typeName) {
        return new PhpType() {
            @Override
            public String toString() {
                return typeName;
            }
        };
    }

    public static PhpType createUnionType(List<PhpType> types) {
        return new PhpType() {
            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder("UnionType(");
                for (int i = 0; i < types.size(); i++) {
                    sb.append(types.get(i).toString());
                    if (i < types.size() - 1) {
                        sb.append(", ");
                    }
                }
                sb.append(")");
                return sb.toString();
            }
        };
    }
}
