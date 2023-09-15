package br.com.codeflix.catalog.admin.domain.video;

public interface MediaResourceGateway {

    AudioVideoMedia storeAudioVideo(VideoID id, Resource resource);

    ImageMedia storeImage(VideoID id, Resource resource);

    void clearResources(VideoID id);
}
