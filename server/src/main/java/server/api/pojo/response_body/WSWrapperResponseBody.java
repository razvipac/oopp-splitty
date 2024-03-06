package server.api.pojo.response_body;

public record WSWrapperResponseBody<T>(
            WSAction action,
            T object
) {
}
