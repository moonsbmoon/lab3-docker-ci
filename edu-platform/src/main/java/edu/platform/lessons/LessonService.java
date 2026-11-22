package edu.platform.lessons;

import edu.platform.catalog.CourseResponse;
import edu.platform.catalog.CourseService;
import edu.platform.shared.ResourceNotFoundException;
import edu.platform.users.UserResponse;
import edu.platform.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;
    private final UserService userService;
    private final CourseService courseService;

    @Transactional
    public LessonResponse create(Long courseId, LessonRequest request) {
        // Проверяем, что курс существует (косвенно проверяем через сервис)
        courseService.findById(courseId);

        Lesson lesson = new Lesson();
        lesson.setCourseId(courseId);
        lesson.setTitle(request.title());
        lesson.setContent(request.content());

        return lessonMapper.toResponse(lessonRepository.save(lesson));
    }

    @Transactional(readOnly = true)
    public List<LessonResponse> getByCourseId(Long courseId) {
        return lessonRepository.findByCourseId(courseId).stream()
                .map(lessonMapper::toResponse)
                .toList();
    }

    @Transactional
    public LessonResponse update(Long id, LessonRequest request, String username) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));

        UserResponse user = userService.getByEmail(username);
        CourseResponse course = courseService.findById(lesson.getCourseId());

        // Права: ADMIN или TEACHER (владелец курса)
        if (!user.role().equals("ADMIN")) {
            if (!user.role().equals("TEACHER") || !course.teacherId().equals(user.id())) {
                throw new AccessDeniedException("You cannot edit this lesson");
            }
        }

        lesson.setTitle(request.title());
        lesson.setContent(request.content());

        return lessonMapper.toResponse(lessonRepository.save(lesson));
    }
}