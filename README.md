# PHP Type Resolver

## Overview

This project implements a **TypeResolver** that determines the type of a PHP variable by parsing `@var` documentation blocks. The solution is written in **Java** and uses only the provided API (no external libraries). It handles standard types, union types, named variable tags, and correctly ignores tags that apply to other variables.

`TypeResolver` implements `inferTypeFromDoc(PhpVariable variable)`, which returns a `PhpType` based solely on the variable’s docblock.

## Expected behaviour

| Case | Example | Result |
|------|---------|--------|
| Standard type | `/** @var User */` for `$user` | `User` |
| Union type | `/** @var string\|int */` for `$id` | `UnionType(string, int)` |
| Named tag | `/** @var Logger $log */` for `$log` | `Logger` |
| Name mismatch | `/** @var Admin $adm */` for `$guest` | `mixed` |
| Multiple tags | `@var int $id` and `@var string $name`, inspecting `$name` | `string` |
| No DocBlock / no match | — | `mixed` |

## Technical approach

The implementation uses priority-based logic:

1. **Named tag priority** — If a tag includes a variable name (e.g. `@var User $currentUser`), the resolver matches it to the inspected variable. If they match, that type is returned immediately.
2. **Unnamed fallback** — If a tag has no variable name (e.g. `@var string|int`), it is treated as a candidate for the variable being inspected.
3. **Union type parsing** — The pipe `|` in the type string is detected and `TypeFactory.createUnionType` is used to build a composite type.
4. **Robustness** — Extra whitespace and trailing descriptions in tag values are handled.

## PHP-specific considerations

What the code actually does:

- **Variable filtering** — If a tag has a variable name (e.g. `@var Admin $adm`) and it does not match the variable being resolved, that tag is ignored.
- **Union types** — The type string is split on `|` and built as a union. The type is taken as the **first whitespace-separated token** of the tag value, so the union must be in that single token: `string|int` works, but `string | int` does not (only `string` would be used).
- **Nullable** — `?User` is one token and kept as a single type name, `User|null` is one token, split on `|`, so parsed as a union.
- **Generic and shaped types** — Not parsed. The type is whatever is in the first token. If that token is e.g. `array<int,string>` (no space after comma), it is passed through as-is. If the type contains spaces (e.g. `array<int, string>`), only the part before the first space is used.
- **Unnamed @var** — A tag with no `$variableName` is used as a fallback for the variable being inspected (first such tag wins).

## Project structure

- **`TypeResolver.java`** — Main logic, contains `inferTypeFromDoc(PhpVariable variable)`.
- **`Php.java`** — API interfaces (`PhpVariable`, `PhpDocBlock`, `DocTag`, `PhpType`) and `TypeFactory`.
- **`TypeResolverTest.java`** — JUnit test suite.

## Testing

Run the **JUnit 5** tests in `TypeResolverTest`

