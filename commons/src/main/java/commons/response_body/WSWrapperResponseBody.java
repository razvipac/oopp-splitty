package commons.response_body;

public record WSWrapperResponseBody<T>(
            WSAction action,
            T object
) {
}
