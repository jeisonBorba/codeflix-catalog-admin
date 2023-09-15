package br.com.codeflix.catalog.admin.application.video.retrieve.list;

import br.com.codeflix.catalog.admin.application.UseCase;
import br.com.codeflix.catalog.admin.domain.pagination.Pagination;
import br.com.codeflix.catalog.admin.domain.video.VideoSearchQuery;

public abstract class ListVideoUseCase extends UseCase<VideoSearchQuery, Pagination<VideoListOutput>> {
}
