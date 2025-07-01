package asis.dto;

import asis.models.Barrio;
import asis.models.Campaña;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

public class CampaniaMapper {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public static CampaniaDTO toDTO(Campaña campaña) {
        return new CampaniaDTO(
                campaña.getId(),
                campaña.getName(),
                sdf.format(campaña.getStartDate()),
                sdf.format(campaña.getEndDate()),
                campaña.getBarrio() != null ? campaña.getBarrio().getId() : null
        );
    }

    public static List<CampaniaDTO> toDTOList(List<Campaña> campañas) {
        return campañas.stream().map(CampaniaMapper::toDTO).collect(Collectors.toList());
    }

    public static Campaña toEntity(CampaniaDTO dto) throws Exception {
        Campaña campaña = new Campaña();
        campaña.setId(dto.getId());
        campaña.setName(dto.getName());
        campaña.setStartDate(sdf.parse(dto.getStartDate()));
        campaña.setEndDate(sdf.parse(dto.getEndDate()));
        if (dto.getBarrioId() != null) {
            Barrio barrio = new Barrio();
            barrio.setId(dto.getBarrioId());
            campaña.setBarrio(barrio);
        }
        return campaña;
    }
}
