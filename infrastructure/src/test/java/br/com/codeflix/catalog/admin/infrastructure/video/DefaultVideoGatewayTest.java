package br.com.codeflix.catalog.admin.infrastructure.video;

import br.com.codeflix.catalog.admin.IntegrationTest;
import br.com.codeflix.catalog.admin.domain.castmember.CastMember;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberGateway;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberID;
import br.com.codeflix.catalog.admin.domain.category.Category;
import br.com.codeflix.catalog.admin.domain.category.CategoryGateway;
import br.com.codeflix.catalog.admin.domain.category.CategoryID;
import br.com.codeflix.catalog.admin.domain.genre.Genre;
import br.com.codeflix.catalog.admin.domain.genre.GenreGateway;
import br.com.codeflix.catalog.admin.domain.genre.GenreID;
import br.com.codeflix.catalog.admin.domain.video.*;
import br.com.codeflix.catalog.admin.infrastructure.video.persistence.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.Set;

import static br.com.codeflix.catalog.admin.domain.category.Fixture.CastMembers.gabriel;
import static br.com.codeflix.catalog.admin.domain.category.Fixture.CastMembers.wesley;
import static br.com.codeflix.catalog.admin.domain.category.Fixture.Categories.aulas;
import static br.com.codeflix.catalog.admin.domain.category.Fixture.Categories.lives;
import static br.com.codeflix.catalog.admin.domain.category.Fixture.Genres.business;
import static br.com.codeflix.catalog.admin.domain.category.Fixture.Genres.tech;
import static br.com.codeflix.catalog.admin.domain.category.Fixture.*;
import static br.com.codeflix.catalog.admin.domain.category.Fixture.Videos.description;
import static br.com.codeflix.catalog.admin.domain.category.Fixture.Videos.rating;
import static org.junit.jupiter.api.Assertions.*;

@IntegrationTest
public class DefaultVideoGatewayTest {

    @Autowired
    private DefaultVideoGateway videoGateway;

    @Autowired
    private CastMemberGateway castMemberGateway;

    @Autowired
    private CategoryGateway categoryGateway;

    @Autowired
    private GenreGateway genreGateway;

    @Autowired
    private VideoRepository videoRepository;

    private CastMember wesley;
    private CastMember gabriel;

    private Category aulas;
    private Category lives;

    private Genre tech;
    private Genre business;

    @BeforeEach
    public void setUp() {
        wesley = castMemberGateway.create(wesley());
        gabriel = castMemberGateway.create(gabriel());

        aulas = categoryGateway.create(aulas());
        lives = categoryGateway.create(lives());

        tech = genreGateway.create(tech());
        business = genreGateway.create(business());
    }

    @Test
    public void testInjection() {
        assertNotNull(videoGateway);
        assertNotNull(castMemberGateway);
        assertNotNull(categoryGateway);
        assertNotNull(genreGateway);
        assertNotNull(videoRepository);
    }

    @Test
    @Transactional
    public void givenAValidVideo_whenCallsCreate_shouldPersistIt() {
        final var expectedTitle = title();
        final var expectedDescription = description();
        final var expectedLaunchYear = Year.of(year());
        final var expectedDuration = duration();
        final var expectedOpened = bool();
        final var expectedPublished = bool();
        final var expectedRating = rating();
        final var expectedCategories = Set.of(aulas.getId());
        final var expectedGenres = Set.of(tech.getId());
        final var expectedMembers = Set.of(wesley.getId());

        final AudioVideoMedia expectedVideo = AudioVideoMedia.with("123", "video", "/media/video");

        final AudioVideoMedia expectedTrailer = AudioVideoMedia.with("123", "trailer", "/media/trailer");

        final ImageMedia expectedBanner = ImageMedia.with("123", "banner", "/media/banner");

        final ImageMedia expectedThumb = ImageMedia.with("123", "thumb", "/media/thumb");

        final ImageMedia expectedThumbHalf = ImageMedia.with("123", "thumbHalf", "/media/thumbHalf");

        final var video = Video.newVideo(
                        expectedTitle,
                        expectedDescription,
                        expectedLaunchYear,
                        expectedDuration,
                        expectedOpened,
                        expectedPublished,
                        expectedRating,
                        expectedCategories,
                        expectedGenres,
                        expectedMembers
                )
                .updateVideoMedia(expectedVideo)
                .updateTrailerMedia(expectedTrailer)
                .updateBannerMedia(expectedBanner)
                .updateThumbnailMedia(expectedThumb)
                .updateThumbnailHalfMedia(expectedThumbHalf);

        final var actualVideo = videoGateway.create(video);

        assertNotNull(actualVideo);
        assertNotNull(actualVideo.getId());

        assertEquals(expectedTitle, actualVideo.getTitle());
        assertEquals(expectedDescription, actualVideo.getDescription());
        assertEquals(expectedLaunchYear, actualVideo.getLaunchedAt());
        assertEquals(expectedDuration, actualVideo.getDuration());
        assertEquals(expectedOpened, actualVideo.getOpened());
        assertEquals(expectedPublished, actualVideo.getPublished());
        assertEquals(expectedRating, actualVideo.getRating());
        assertEquals(expectedCategories, actualVideo.getCategories());
        assertEquals(expectedGenres, actualVideo.getGenres());
        assertEquals(expectedMembers, actualVideo.getCastMembers());
        assertEquals(expectedVideo.name(), actualVideo.getVideo().get().name());
        assertEquals(expectedTrailer.name(), actualVideo.getTrailer().get().name());
        assertEquals(expectedBanner.name(), actualVideo.getBanner().get().name());
        assertEquals(expectedThumb.name(), actualVideo.getThumbnail().get().name());
        assertEquals(expectedThumbHalf.name(), actualVideo.getThumbnailHalf().get().name());

        final var persistedVideo = videoRepository.findById(actualVideo.getId().getValue()).get();

        assertEquals(expectedTitle, persistedVideo.getTitle());
        assertEquals(expectedDescription, persistedVideo.getDescription());
        assertEquals(expectedLaunchYear, Year.of(persistedVideo.getYearLaunched()));
        assertEquals(expectedDuration, persistedVideo.getDuration());
        assertEquals(expectedOpened, persistedVideo.isOpened());
        assertEquals(expectedPublished, persistedVideo.isPublished());
        assertEquals(expectedRating, persistedVideo.getRating());
        assertEquals(expectedCategories, persistedVideo.getCategoriesID());
        assertEquals(expectedGenres, persistedVideo.getGenresID());
        assertEquals(expectedMembers, persistedVideo.getCastMembersID());
        assertEquals(expectedVideo.name(), persistedVideo.getVideo().getName());
        assertEquals(expectedTrailer.name(), persistedVideo.getTrailer().getName());
        assertEquals(expectedBanner.name(), persistedVideo.getBanner().getName());
        assertEquals(expectedThumb.name(), persistedVideo.getThumbnail().getName());
        assertEquals(expectedThumbHalf.name(), persistedVideo.getThumbnailHalf().getName());
    }

    @Test
    @Transactional
    public void givenAValidVideoWithoutRelations_whenCallsCreate_shouldPersistIt() {
        final var expectedTitle = title();
        final var expectedDescription = Videos.description();
        final var expectedLaunchYear = Year.of(year());
        final var expectedDuration = duration();
        final var expectedOpened = bool();
        final var expectedPublished = bool();
        final var expectedRating = Videos.rating();
        final var expectedCategories = Set.<CategoryID>of();
        final var expectedGenres = Set.<GenreID>of();
        final var expectedMembers = Set.<CastMemberID>of();

        final var video = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchYear,
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating,
                expectedCategories,
                expectedGenres,
                expectedMembers
        );

        final var actualVideo = videoGateway.create(video);

        assertNotNull(actualVideo);
        assertNotNull(actualVideo.getId());

        assertEquals(expectedTitle, actualVideo.getTitle());
        assertEquals(expectedDescription, actualVideo.getDescription());
        assertEquals(expectedLaunchYear, actualVideo.getLaunchedAt());
        assertEquals(expectedDuration, actualVideo.getDuration());
        assertEquals(expectedOpened, actualVideo.getOpened());
        assertEquals(expectedPublished, actualVideo.getPublished());
        assertEquals(expectedRating, actualVideo.getRating());
        assertEquals(expectedCategories, actualVideo.getCategories());
        assertEquals(expectedGenres, actualVideo.getGenres());
        assertEquals(expectedMembers, actualVideo.getCastMembers());
        assertTrue(actualVideo.getVideo().isEmpty());
        assertTrue(actualVideo.getTrailer().isEmpty());
        assertTrue(actualVideo.getBanner().isEmpty());
        assertTrue(actualVideo.getThumbnail().isEmpty());
        assertTrue(actualVideo.getThumbnailHalf().isEmpty());

        final var persistedVideo = videoRepository.findById(actualVideo.getId().getValue()).get();

        assertEquals(expectedTitle, persistedVideo.getTitle());
        assertEquals(expectedDescription, persistedVideo.getDescription());
        assertEquals(expectedLaunchYear, Year.of(persistedVideo.getYearLaunched()));
        assertEquals(expectedDuration, persistedVideo.getDuration());
        assertEquals(expectedOpened, persistedVideo.isOpened());
        assertEquals(expectedPublished, persistedVideo.isPublished());
        assertEquals(expectedRating, persistedVideo.getRating());
        assertEquals(expectedCategories, persistedVideo.getCategoriesID());
        assertEquals(expectedGenres, persistedVideo.getGenresID());
        assertEquals(expectedMembers, persistedVideo.getCastMembersID());
        assertNull(persistedVideo.getVideo());
        assertNull(persistedVideo.getTrailer());
        assertNull(persistedVideo.getBanner());
        assertNull(persistedVideo.getThumbnail());
        assertNull(persistedVideo.getThumbnailHalf());
    }

    @Test
    @Transactional
    public void givenAValidVideo_whenCallsUpdate_shouldPersistIt() {
        final var video = videoGateway.create(Video.newVideo(
                title(),
                description(),
                Year.of(year()),
                duration(),
                bool(),
                bool(),
                rating(),
                Set.of(),
                Set.of(),
                Set.of()
        ));

        final var expectedTitle = title();
        final var expectedDescription = description();
        final var expectedLaunchYear = Year.of(year());
        final var expectedDuration = duration();
        final var expectedOpened = bool();
        final var expectedPublished = bool();
        final var expectedRating = rating();
        final var expectedCategories = Set.of(aulas.getId());
        final var expectedGenres = Set.of(tech.getId());
        final var expectedMembers = Set.of(wesley.getId());

        final AudioVideoMedia expectedVideo = AudioVideoMedia.with("123", "video", "/media/video");

        final AudioVideoMedia expectedTrailer = AudioVideoMedia.with("123", "trailer", "/media/trailer");

        final ImageMedia expectedBanner = ImageMedia.with("123", "banner", "/media/banner");

        final ImageMedia expectedThumb = ImageMedia.with("123", "thumb", "/media/thumb");

        final ImageMedia expectedThumbHalf = ImageMedia.with("123", "thumbHalf", "/media/thumbHalf");

        final var updatedVideo = Video.with(video)
                .update(
                        expectedTitle,
                        expectedDescription,
                        expectedLaunchYear,
                        expectedDuration,
                        expectedOpened,
                        expectedPublished,
                        expectedRating,
                        expectedCategories,
                        expectedGenres,
                        expectedMembers
                )
                .updateVideoMedia(expectedVideo)
                .updateTrailerMedia(expectedTrailer)
                .updateBannerMedia(expectedBanner)
                .updateThumbnailMedia(expectedThumb)
                .updateThumbnailHalfMedia(expectedThumbHalf);

        final var actualVideo = videoGateway.update(updatedVideo);

        assertNotNull(actualVideo);
        assertNotNull(actualVideo.getId());

        assertEquals(expectedTitle, actualVideo.getTitle());
        assertEquals(expectedDescription, actualVideo.getDescription());
        assertEquals(expectedLaunchYear, actualVideo.getLaunchedAt());
        assertEquals(expectedDuration, actualVideo.getDuration());
        assertEquals(expectedOpened, actualVideo.getOpened());
        assertEquals(expectedPublished, actualVideo.getPublished());
        assertEquals(expectedRating, actualVideo.getRating());
        assertEquals(expectedCategories, actualVideo.getCategories());
        assertEquals(expectedGenres, actualVideo.getGenres());
        assertEquals(expectedMembers, actualVideo.getCastMembers());
        assertEquals(expectedVideo.name(), actualVideo.getVideo().get().name());
        assertEquals(expectedTrailer.name(), actualVideo.getTrailer().get().name());
        assertEquals(expectedBanner.name(), actualVideo.getBanner().get().name());
        assertEquals(expectedThumb.name(), actualVideo.getThumbnail().get().name());
        assertEquals(expectedThumbHalf.name(), actualVideo.getThumbnailHalf().get().name());
        assertNotNull(actualVideo.getCreatedAt());
        assertTrue(actualVideo.getUpdatedAt().isAfter(video.getUpdatedAt()));

        final var persistedVideo = videoRepository.findById(actualVideo.getId().getValue()).get();

        assertEquals(expectedTitle, persistedVideo.getTitle());
        assertEquals(expectedDescription, persistedVideo.getDescription());
        assertEquals(expectedLaunchYear, Year.of(persistedVideo.getYearLaunched()));
        assertEquals(expectedDuration, persistedVideo.getDuration());
        assertEquals(expectedOpened, persistedVideo.isOpened());
        assertEquals(expectedPublished, persistedVideo.isPublished());
        assertEquals(expectedRating, persistedVideo.getRating());
        assertEquals(expectedCategories, persistedVideo.getCategoriesID());
        assertEquals(expectedGenres, persistedVideo.getGenresID());
        assertEquals(expectedMembers, persistedVideo.getCastMembersID());
        assertEquals(expectedVideo.name(), persistedVideo.getVideo().getName());
        assertEquals(expectedTrailer.name(), persistedVideo.getTrailer().getName());
        assertEquals(expectedBanner.name(), persistedVideo.getBanner().getName());
        assertEquals(expectedThumb.name(), persistedVideo.getThumbnail().getName());
        assertEquals(expectedThumbHalf.name(), persistedVideo.getThumbnailHalf().getName());
        assertNotNull(persistedVideo.getCreatedAt());
        assertTrue(persistedVideo.getUpdatedAt().isAfter(video.getUpdatedAt()));
    }

    @Test
    public void givenAValidVideoId_whenCallsDeleteById_shouldDeleteIt() {
        final var video = videoGateway.create(Video.newVideo(
                title(),
                description(),
                Year.of(year()),
                duration(),
                bool(),
                bool(),
                rating(),
                Set.of(),
                Set.of(),
                Set.of()
        ));

        assertEquals(1, videoRepository.count());

        final var id = video.getId();

        videoGateway.deleteById(id);

        assertEquals(0, videoRepository.count());
    }

    @Test
    public void givenAnInvalidVideoId_whenCallsDeleteById_shouldDeleteIt() {
        videoGateway.create(Video.newVideo(
                title(),
                description(),
                Year.of(year()),
                duration(),
                bool(),
                bool(),
                rating(),
                Set.of(),
                Set.of(),
                Set.of()
        ));

        assertEquals(1, videoRepository.count());

        final var id = VideoID.unique();

        videoGateway.deleteById(id);

        assertEquals(1, videoRepository.count());
    }

    @Test
    public void givenAValidVideo_whenCallsFindById_shouldReturnIt() {
        final var expectedTitle = title();
        final var expectedDescription = description();
        final var expectedLaunchYear = Year.of(year());
        final var expectedDuration = duration();
        final var expectedOpened = bool();
        final var expectedPublished = bool();
        final var expectedRating = rating();
        final var expectedCategories = Set.of(aulas.getId());
        final var expectedGenres = Set.of(tech.getId());
        final var expectedMembers = Set.of(wesley.getId());

        final AudioVideoMedia expectedVideo = AudioVideoMedia.with("123", "video", "/media/video");

        final AudioVideoMedia expectedTrailer = AudioVideoMedia.with("123", "trailer", "/media/trailer");

        final ImageMedia expectedBanner = ImageMedia.with("123", "banner", "/media/banner");

        final ImageMedia expectedThumb = ImageMedia.with("123", "thumb", "/media/thumb");

        final ImageMedia expectedThumbHalf = ImageMedia.with("123", "thumbHalf", "/media/thumbHalf");

        final var video = videoGateway.create(
                Video.newVideo(
                                expectedTitle,
                                expectedDescription,
                                expectedLaunchYear,
                                expectedDuration,
                                expectedOpened,
                                expectedPublished,
                                expectedRating,
                                expectedCategories,
                                expectedGenres,
                                expectedMembers
                        )
                        .updateVideoMedia(expectedVideo)
                        .updateTrailerMedia(expectedTrailer)
                        .updateBannerMedia(expectedBanner)
                        .updateThumbnailMedia(expectedThumb)
                        .updateThumbnailHalfMedia(expectedThumbHalf)
        );

        final var actualVideo = videoGateway.findById(video.getId()).get();

        assertNotNull(actualVideo);
        assertNotNull(actualVideo.getId());

        assertEquals(expectedTitle, actualVideo.getTitle());
        assertEquals(expectedDescription, actualVideo.getDescription());
        assertEquals(expectedLaunchYear, actualVideo.getLaunchedAt());
        assertEquals(expectedDuration, actualVideo.getDuration());
        assertEquals(expectedOpened, actualVideo.getOpened());
        assertEquals(expectedPublished, actualVideo.getPublished());
        assertEquals(expectedRating, actualVideo.getRating());
        assertEquals(expectedCategories, actualVideo.getCategories());
        assertEquals(expectedGenres, actualVideo.getGenres());
        assertEquals(expectedMembers, actualVideo.getCastMembers());
        assertEquals(expectedVideo.name(), actualVideo.getVideo().get().name());
        assertEquals(expectedTrailer.name(), actualVideo.getTrailer().get().name());
        assertEquals(expectedBanner.name(), actualVideo.getBanner().get().name());
        assertEquals(expectedThumb.name(), actualVideo.getThumbnail().get().name());
        assertEquals(expectedThumbHalf.name(), actualVideo.getThumbnailHalf().get().name());
    }

    @Test
    public void givenAnInvalidVideoId_whenCallsFindById_shouldEmpty() {
        videoGateway.create(Video.newVideo(
                title(),
                description(),
                Year.of(year()),
                duration(),
                bool(),
                bool(),
                Videos.rating(),
                Set.of(),
                Set.of(),
                Set.of()
        ));

        final var id = VideoID.unique();

        final var actualVideo = videoGateway.findById(id);

        assertTrue(actualVideo.isEmpty());
    }

    @Test
    public void givenEmptyParams_whenCallFindAll_shouldReturnAllList() {
        mockVideos();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 4;

        final var query = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(),
                Set.of(),
                Set.of()
        );

        final var actualPage = videoGateway.findAll(query);

        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedTotal, actualPage.items().size());
    }

    @Test
    public void givenEmptyVideos_whenCallFindAll_shouldReturnEmptyList() {
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;

        final var query = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(),
                Set.of(),
                Set.of()
        );

        final var actualPage = videoGateway.findAll(query);

        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedTotal, actualPage.items().size());
    }

    @Test
    public void givenAValidCategory_whenCallFindAll_shouldReturnFilteredList() {
        mockVideos();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 2;

        final var query = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(),
                Set.of(aulas.getId()),
                Set.of()
        );

        final var actualPage = videoGateway.findAll(query);

        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedTotal, actualPage.items().size());

        assertEquals("21.1 Implementação dos testes integrados do findAll", actualPage.items().get(0).title());
        assertEquals("Aula de empreendedorismo", actualPage.items().get(1).title());
    }

    @Test
    public void givenAValidCastMember_whenCallFindAll_shouldReturnFilteredList() {
        mockVideos();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 2;

        final var query = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(wesley.getId()),
                Set.of(),
                Set.of()
        );

        final var actualPage = videoGateway.findAll(query);

        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedTotal, actualPage.items().size());

        assertEquals("Aula de empreendedorismo", actualPage.items().get(0).title());
        assertEquals("System Design no Mercado Livre na prática", actualPage.items().get(1).title());
    }

    @Test
    public void givenAValidGenre_whenCallFindAll_shouldReturnFilteredList() {
        mockVideos();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 1;

        final var query = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(),
                Set.of(),
                Set.of(business.getId())
        );

        final var actualPage = videoGateway.findAll(query);

        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedTotal, actualPage.items().size());

        assertEquals("Aula de empreendedorismo", actualPage.items().get(0).title());
    }

    @Test
    public void givenAllParameters_whenCallFindAll_shouldReturnFilteredList() {
        mockVideos();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "empreendedorismo";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 1;

        final var query = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(wesley.getId()),
                Set.of(aulas.getId()),
                Set.of(business.getId())
        );

        final var actualPage = videoGateway.findAll(query);

        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedTotal, actualPage.items().size());

        assertEquals("Aula de empreendedorismo", actualPage.items().get(0).title());
    }

    @ParameterizedTest
    @CsvSource({
            "0,2,2,4,21.1 Implementação dos testes integrados do findAll;Aula de empreendedorismo",
            "1,2,2,4,Não cometa esses erros ao trabalhar com Microsserviços;System Design no Mercado Livre na prática",
    })
    public void givenAValidPaging_whenCallsFindAll_shouldReturnPaged(
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedVideos
    ) {
        mockVideos();

        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";

        final var query = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(),
                Set.of(),
                Set.of()
        );

        final var actualPage = videoGateway.findAll(query);

        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedItemsCount, actualPage.items().size());

        int index = 0;
        for (final var expectedTitle : expectedVideos.split(";")) {
            final var actualTitle = actualPage.items().get(index).title();
            assertEquals(expectedTitle, actualTitle);
            index++;
        }
    }

    @ParameterizedTest
    @CsvSource({
            "system,0,10,1,1,System Design no Mercado Livre na prática",
            "microsser,0,10,1,1,Não cometa esses erros ao trabalhar com Microsserviços",
            "empreendedorismo,0,10,1,1,Aula de empreendedorismo",
            "21,0,10,1,1,21.1 Implementação dos testes integrados do findAll",
    })
    public void givenAValidTerm_whenCallsFindAll_shouldReturnFiltered(
            final String expectedTerms,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedVideo
    ) {
        mockVideos();

        final var expectedSort = "title";
        final var expectedDirection = "asc";

        final var query = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(),
                Set.of(),
                Set.of()
        );

        final var actualPage = videoGateway.findAll(query);

        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedItemsCount, actualPage.items().size());
        assertEquals(expectedVideo, actualPage.items().get(0).title());
    }

    @ParameterizedTest
    @CsvSource({
            "title,asc,0,10,4,4,21.1 Implementação dos testes integrados do findAll",
            "title,desc,0,10,4,4,System Design no Mercado Livre na prática",
            "createdAt,asc,0,10,4,4,System Design no Mercado Livre na prática",
            "createdAt,desc,0,10,4,4,Aula de empreendedorismo",
    })
    public void givenAValidSortAndDirection_whenCallsFindAll_shouldReturnOrdered(
            final String expectedSort,
            final String expectedDirection,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedVideo
    ) {
        mockVideos();

        final var expectedTerms = "";

        final var query = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(),
                Set.of(),
                Set.of()
        );

        final var actualPage = videoGateway.findAll(query);

        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedItemsCount, actualPage.items().size());
        assertEquals(expectedVideo, actualPage.items().get(0).title());
    }

    private void mockVideos() {
        videoGateway.create(Video.newVideo(
                "System Design no Mercado Livre na prática",
                description(),
                Year.of(2022),
                duration(),
                bool(),
                bool(),
                rating(),
                Set.of(lives.getId()),
                Set.of(tech.getId()),
                Set.of(wesley.getId(), gabriel.getId())
        ));

        videoGateway.create(Video.newVideo(
                "Não cometa esses erros ao trabalhar com Microsserviços",
                description(),
                Year.of(year()),
                duration(),
                bool(),
                bool(),
                rating(),
                Set.of(),
                Set.of(),
                Set.of()
        ));

        videoGateway.create(Video.newVideo(
                "21.1 Implementação dos testes integrados do findAll",
                description(),
                Year.of(year()),
                duration(),
                bool(),
                bool(),
                rating(),
                Set.of(aulas.getId()),
                Set.of(tech.getId()),
                Set.of(gabriel.getId())
        ));

        videoGateway.create(Video.newVideo(
                "Aula de empreendedorismo",
                description(),
                Year.of(year()),
                duration(),
                bool(),
                bool(),
                rating(),
                Set.of(aulas.getId()),
                Set.of(business.getId()),
                Set.of(wesley.getId())
        ));
    }
}