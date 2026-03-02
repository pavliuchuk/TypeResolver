import java.util.ArrayList;
import java.util.List;

public class TypeResolver {

    public PhpType inferTypeFromDoc(PhpVariable variable) {
        PhpDocBlock docBlock = variable.getDocBlock();
        if (docBlock == null) {
            return TypeFactory.createType("mixed");
        }

        String targetName = variable.getName();
        List<DocTag> varTags = docBlock.getTagsByName("@var");
        if (varTags == null) {
            return TypeFactory.createType("mixed");
        }

        PhpType unnamedFallback = null;
        for (DocTag tag : varTags) {
            String value = tag.getValue().trim();
            if (value.isEmpty())
                continue;

            String[] parts = value.split("\\s+");
            String typeString = parts[0];

            if (parts.length >= 2 && parts[1].startsWith("$")) {
                if (parts[1].equals(targetName)) {
                    return parseTypeString(typeString);
                }
                continue;
            }

            if (unnamedFallback == null) {
                unnamedFallback = parseTypeString(typeString);
            }
        }

        return unnamedFallback != null ? unnamedFallback : TypeFactory.createType("mixed");
    }

    private PhpType parseTypeString(String typeString) {
        if (typeString.contains("|")) {
            String[] types = typeString.split("\\|");
            List<PhpType> phpTypes = new ArrayList<>();
            for (String t : types) {
                if (!t.trim().isEmpty()) {
                    phpTypes.add(TypeFactory.createType(t.trim()));
                }
            }
            return TypeFactory.createUnionType(phpTypes);
        }
        return TypeFactory.createType(typeString.trim());
    }
}
