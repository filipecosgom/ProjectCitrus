package pt.uc.dei.unit.mapper;

import pt.uc.dei.dtos.AppraisalDTO;
import pt.uc.dei.dtos.AppraisalResponseDTO;
import pt.uc.dei.entities.AppraisalEntity;
import pt.uc.dei.mapper.AppraisalMapper;

import java.util.Collections;
import java.util.List;

public class AppraisalMapperMock implements AppraisalMapper {
    @Override public AppraisalDTO toDto(AppraisalEntity appraisalEntity) { return null; }
    @Override public AppraisalResponseDTO toResponseDto(AppraisalEntity appraisalEntity) { return null; }
    @Override public AppraisalEntity toEntity(AppraisalDTO appraisalDTO) { return null; }
    @Override public List<AppraisalDTO> toDtoList(List<AppraisalEntity> appraisals) { return Collections.emptyList(); }
    @Override public List<AppraisalEntity> toEntityList(List<AppraisalDTO> appraisalDTOs) { return Collections.emptyList(); }
    @Override public void updateEntityFromDto(AppraisalDTO appraisalDTO, AppraisalEntity appraisalEntity) {}
}
