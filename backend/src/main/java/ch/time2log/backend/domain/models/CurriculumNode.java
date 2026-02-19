package ch.time2log.backend.domain.models;

import ch.time2log.backend.infrastructure.supabase.responses.CurriculumNodeResponse;

import java.util.List;
import java.util.UUID;

public record CurriculumNode(
        UUID id,
        UUID parentId,
        String nodeType,
        String key,
        String label,
        String description,
        Integer sortOrder,
        Boolean active
) {
    public static CurriculumNode of(CurriculumNodeResponse response) {
        return new CurriculumNode(
                response.id(),
                response.parent_id(),
                response.node_type(),
                response.key(),
                response.label(),
                response.description(),
                response.sort_order(),
                response.is_active()
        );
    }

    public static List<CurriculumNode> ofList(List<CurriculumNodeResponse> responses) {
        return responses.stream().map(CurriculumNode::of).toList();
    }
}
