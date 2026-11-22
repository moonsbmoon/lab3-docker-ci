package edu.platform.lessons;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses/{courseId}/lessons")
@RequiredArgsConstructor
@Tag(name = "Lessons")
public class LessonController {

    private final LessonService lessonService;

    @GetMapping
    public List<LessonResponse> getLessons(@PathVariable Long courseId) {
        return lessonService.getByCourseId(courseId);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public LessonResponse create(@PathVariable Long courseId, @RequestBody LessonRequest request) {
        return lessonService.create(courseId, request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public LessonResponse update(@PathVariable Long courseId, @PathVariable Long id,
                                 @RequestBody LessonRequest request, Authentication auth) {
        return lessonService.update(id, request, auth.getName());
    }
}