package hexlet.code.mapper;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        uses = {JsonNullableMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
@SuppressWarnings("java:S6813")
public abstract class TaskMapper {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    private static final String ACTION_NOT_FOUNT = " not found";

    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "slugToTaskStatus")
    @Mapping(target = "assignee", source = "assigneeId", qualifiedByName = "idToUser")
    @Mapping(target = "labels", source = "taskLabelIds", qualifiedByName = "idsToLabels")
    public abstract Task toEntity(TaskCreateDTO dto);

    @Mapping(target = "status", source = "taskStatus.slug")
    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "taskLabelIds", source = "labels", qualifiedByName = "labelsToIds")
    public abstract TaskDTO toDTO(Task task);

    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "slugToTaskStatusNullable")
    @Mapping(target = "assignee", source = "assigneeId", qualifiedByName = "idToUserNullable")
    @Mapping(target = "labels", source = "taskLabelIds", qualifiedByName = "idsToLabelsNullable")
    public abstract void updateEntity(TaskUpdateDTO dto, @MappingTarget Task task);

    @Named("slugToTaskStatus")
    protected TaskStatus slugToTaskStatus(String slug) {
        if (slug == null) {
            return null;
        }
        return taskStatusRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("TaskStatus with slug " + slug + ACTION_NOT_FOUNT));
    }

    @Named("slugToTaskStatusNullable")
    protected TaskStatus slugToTaskStatusNullable(
            org.openapitools.jackson.nullable.JsonNullable<String> slugNullable) {
        return Optional.ofNullable(slugNullable)
                .filter(JsonNullable::isPresent)
                .map(JsonNullable::get)
                .map(slug -> taskStatusRepository.findBySlug(slug)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("TaskStatus with slug '" + slug + ACTION_NOT_FOUNT)))
                .orElse(null);
    }

    @Named("idToUser")
    protected User idToUser(Long id) {
        if (id == null) {
            return null;
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + ACTION_NOT_FOUNT));
    }

    @Named("idToUserNullable")
    protected User idToUserNullable(
            org.openapitools.jackson.nullable.JsonNullable<Long> idNullable) {
        return Optional.ofNullable(idNullable)
                .filter(JsonNullable::isPresent)
                .map(JsonNullable::get)
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + ACTION_NOT_FOUNT)))
                .orElse(null);
    }

    @Named("idsToLabels")
    protected Set<Label> idsToLabels(Set<Long> ids) {
        if (ids == null) {
            return new HashSet<>();
        }
        return new HashSet<>(labelRepository.findAllById(ids));
    }

    @Named("labelsToIds")
    protected Set<Long> labelsToIds(Set<Label> labels) {
        if (labels == null) {
            return new HashSet<>();
        }
        return labels.stream().map(Label::getId).collect(Collectors.toSet());
    }

    @Named("idsToLabelsNullable")
    protected Set<Label> idsToLabelsNullable(
            org.openapitools.jackson.nullable.JsonNullable<Set<Long>> idsNullable) {
        return Optional.ofNullable(idsNullable)
                .filter(JsonNullable::isPresent)
                .map(JsonNullable::get)
                .map(this::idsToLabels)
                .orElse(Collections.emptySet());
    }
}
