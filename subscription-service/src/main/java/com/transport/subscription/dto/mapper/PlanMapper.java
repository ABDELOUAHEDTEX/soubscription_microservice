package com.transport.subscription.dto.mapper;

import com.transport.subscription.dto.response.PlanResponse;
import com.transport.subscription.model.Plan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper MapStruct pour convertir entre Plan (Entity) et PlanResponse (DTO)
 * MapStruct génère automatiquement l'implémentation à la compilation
 */
@Mapper(componentModel = "spring")
public interface PlanMapper {

    /**
     * Convertit une entité Plan en PlanResponse
     * @param plan Entité Plan
     * @return PlanResponse
     */
    PlanResponse toResponse(Plan plan);

    /**
     * Convertit une liste de Plans en liste de PlanResponses
     * @param plans Liste d'entités Plan
     * @return Liste de PlanResponses
     */
    List<PlanResponse> toResponseList(List<Plan> plans);
}

