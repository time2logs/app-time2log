package ch.time2log.backend.domain.models;

import java.util.UUID;

public record CurriculumNodeFilter(UUID professionId, UUID parentId, String nodeType) {

    public String toQuerySegment() {
        StringBuilder sb = new StringBuilder();

        if (professionId != null) {
            sb.append("&profession_id=eq.").append(professionId);
        }

        if (parentId != null) {
            sb.append("&parent_id=eq.").append(parentId);
        } else if (nodeType == null || nodeType.isBlank()) {
            sb.append("&parent_id=is.null");
        }

        if (nodeType != null && !nodeType.isBlank()) {
            sb.append("&node_type=eq.").append(nodeType.trim());
        }

        return sb.toString();
    }
}
