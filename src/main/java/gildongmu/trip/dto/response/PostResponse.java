package gildongmu.trip.dto.response;



import gildongmu.trip.domain.Image.entity.Image;
import gildongmu.trip.domain.post.entity.Post;
import gildongmu.trip.domain.tag.entity.Tag;
import gildongmu.trip.dto.TripDate;

import java.util.List;
import java.util.stream.Collectors;

public record PostResponse(
        Long id,
        String title,
        Long userId,
        String nickname,
        String profilePath,
        String destination,
        TripDate tripDate,
        Short numberOfPeople,
        String gender,
        String content,
        String status,
        List<String> tag,
        ImageResponse thumbnail,
        List<ImageResponse> images,
        Long countOfComments,
        Long countOfBookmarks
) {

    public static PostResponse from(Post post, List<Tag> tag, List<Image> images) {
        Image thumbnail = findThumbnail(post.getThumbnail(), images);
        List<String> tagNames = findTagNames(tag);

        long countOfBookmarks = post.getBookmarks() != null ? post.getBookmarks().size() : 0;

        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getWriter().id(),
                post.getWriter().nickname(),
                post.getWriter().profilePath(),
                post.getDestination(),
                TripDate.of(post.getStartDate(), post.getEndDate()),
                post.getParticipants(),
                post.getMemberGender().toString(),
                post.getContent(),
                post.getStatus().getCode(),
                tagNames,
                ImageResponse.from(thumbnail),
                ImageResponse.toList(post.getImages()),
                (long) post.getComments().size(),
                countOfBookmarks
        );
    }

    private static Image findThumbnail(String url, List<Image> images) {
        return images.stream()
            .filter(image -> image.getUrl().equals(url))
            .findFirst()
            .orElse(null);
    }

    private static List<String> findTagNames(List<Tag> tags) {
        return tags.stream()
            .map(Tag::getTagName)
            .collect(Collectors.toList());
    }
}
