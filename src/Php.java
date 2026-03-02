import java.util.ArrayList;
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
                List<String> names = new ArrayList<>();
                for (PhpType t : types) {
                    names.add(t.toString());
                }
                return "UnionType(" + String.join(", ", names) + ")";
            }
        };
    }
}
