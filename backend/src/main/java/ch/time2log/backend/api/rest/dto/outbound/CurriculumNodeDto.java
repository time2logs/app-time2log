package ch.time2log.backend.api.rest.dto.outbound;

import ch.time2log.backend.domain.models.CurriculumNode;

import java.util.List;
import java.util.UUID;

public record CurriculumNodeDto(
        UUID id,
        UUID parentId,
        String nodeType,
        String key,
        String label,
        String description,
        Integer sortOrder,
        Boolean active
) {
    public static CurriculumNodeDto of(CurriculumNode node) {
        return new CurriculumNodeDto(
                node.id(),
                node.parentId(),
                node.nodeType(),
                node.key(),
                node.label(),
                node.description(),
                node.sortOrder(),
                node.active()
        );
    }

    public static List<CurriculumNodeDto> ofList(List<CurriculumNode> nodes) {
        return nodes.stream().map(CurriculumNodeDto::of).toList();
    }
}
