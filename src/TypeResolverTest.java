import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TypeResolverTest {

    private TypeResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new TypeResolver();
    }

    @Test
    @DisplayName("Standard Type: should return User for $user")
    void testStandardType() {
        PhpVariable var = createMockVariable("$user", "User");
        PhpType result = resolver.inferTypeFromDoc(var);
        assertEquals("User", result.toString());
    }

    @Test
    @DisplayName("Union Type: should return UnionType for string|int")
    void testUnionType() {
        PhpVariable var = createMockVariable("$id", "string|int");
        PhpType result = resolver.inferTypeFromDoc(var);
        assertEquals("UnionType(string, int)", result.toString());
    }

    @Test
    @DisplayName("Named Tag: should return Logger for $log when name matches")
    void testNamedTagMatch() {
        PhpVariable var = createMockVariable("$log", "Logger $log");
        PhpType result = resolver.inferTypeFromDoc(var);
        assertEquals("Logger", result.toString());
    }

    @Test
    @DisplayName("Name Mismatch: should return mixed if tag name does not match variable")
    void testNameMismatch() {
        PhpVariable var = createMockVariable("$guest", "Admin $adm");
        PhpType result = resolver.inferTypeFromDoc(var);
        assertEquals("mixed", result.toString());
    }

    @Test
    @DisplayName("Multiple Tags: should pick the one matching the variable name")
    void testMultipleTags() {
        PhpVariable var = createMockVariable("$name", "int $id", "string $name");
        PhpType result = resolver.inferTypeFromDoc(var);
        assertEquals("string", result.toString());
    }

    @Test
    @DisplayName("Fallback: should return mixed when no DocBlock exists")
    void testNoDocBlock() {
        PhpVariable var = new PhpVariable() {
            public String getName() { return "$any"; }
            public PhpDocBlock getDocBlock() { return null; }
        };
        PhpType result = resolver.inferTypeFromDoc(var);
        assertEquals("mixed", result.toString());
    }

    @Test
    @DisplayName("Handle extra spaces and descriptions")
    void testRobustness() {
        PhpVariable var = createMockVariable("$user", "  User    $user   some description ");
        PhpType result = resolver.inferTypeFromDoc(var);
        assertEquals("User", result.toString());
    }



    private PhpVariable createMockVariable(String varName, String... varTagValues) {
        return new PhpVariable() {
            @Override
            public String getName() { return varName; }

            @Override
            public PhpDocBlock getDocBlock() {
                return new PhpDocBlock() {
                    @Override
                    public List<DocTag> getTagsByName(String tagName) {
                        List<DocTag> tags = new ArrayList<>();
                        if ("@var".equals(tagName)) {
                            for (String val : varTagValues) {
                                tags.add(() -> val);
                            }
                        }
                        return tags;
                    }
                };
            }
        };
    }
}
