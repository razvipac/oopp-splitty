package commons.dto;

public record WSWrapperResponseBody<T>(
            WSAction action,
            T object
) {
}
