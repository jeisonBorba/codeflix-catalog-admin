package br.com.codeflix.catalog.admin.domain.video;

import br.com.codeflix.catalog.admin.domain.ValueObject;
import br.com.codeflix.catalog.admin.domain.resource.Resource;

import java.util.Objects;

public class VideoResource extends ValueObject {

    private final Resource resource;
    private final VideoMediaType type;

    private VideoResource(final Resource resource, final VideoMediaType type) {
        this.resource = Objects.requireNonNull(resource);
        this.type = Objects.requireNonNull(type);
    }

    public static VideoResource with(final Resource resource, final VideoMediaType type) {
        return new VideoResource(resource, type);
    }

    public Resource resource() {
        return resource;
    }

    public VideoMediaType type() {
        return type;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        final VideoResource that = (VideoResource) object;
        return type() == that.type();
    }

    @Override
    public int hashCode() {
        return Objects.hash(type());
    }
}
