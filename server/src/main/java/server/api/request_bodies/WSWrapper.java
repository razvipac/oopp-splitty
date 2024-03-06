package server.api.request_bodies;

public record WSWrapper<T>(
            WSAction action,
            T object
) {
}
