package edu.platform.catalog;

import edu.platform.catalog.CourseRepository;
import edu.platform.catalog.CourseMapper;
import edu.platform.catalog.CourseResponse;
import edu.platform.catalog.CourseRequest;
import edu.platform.catalog.CourseReportDto;
import edu.platform.shared.NotificationService;
import edu.platform.shared.ResourceNotFoundException;
import edu.platform.users.UserResponse;
import edu.platform.users.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private static final Logger log = LoggerFactory.getLogger(CourseService.class);

    private final CourseRepository courseRepository;
    private final UserService userService;
    private final CourseMapper courseMapper;
    private final NotificationService notificationService;

    // 1. Добавляем метод findById (его не хватало для Controller)
    @Transactional(readOnly = true)
    public CourseResponse findById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + id));
        return courseMapper.toResponse(course);
    }

    // 2. Добавляем метод findAll
    @Transactional(readOnly = true)
    public List<CourseResponse> findAll() {
        return courseRepository.findAll().stream()
                .map(courseMapper::toResponse)
                .toList();
    }

    // 3. Метод для N+1 (он у вас уже был)
    @Transactional(readOnly = true)
    public List<CourseResponse> findAllWithLessons() {
        return courseRepository.findAllWithLessons().stream()
                .map(courseMapper::toResponse)
                .toList();
    }

    // 4. Метод создания курса (если он тоже нужен контроллеру)
    @Transactional
    public CourseResponse create(CourseRequest request, String userEmail) {
        UserResponse teacher = userService.getByEmail(userEmail);
        Course course = new Course();
        course.setTitle(request.title());
        course.setSummary(request.summary());
        course.setPrice(request.price());
        course.setTeacherId(teacher.id());
        Course saved = courseRepository.save(course);
        return courseMapper.toResponse(saved);
    }

    // 5. Метод обновления курса
    @Transactional
    public CourseResponse update(Long id, CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + id));

        course.setTitle(request.title());
        course.setSummary(request.summary());
        course.setPrice(request.price());

        return courseMapper.toResponse(courseRepository.save(course));
    }
}