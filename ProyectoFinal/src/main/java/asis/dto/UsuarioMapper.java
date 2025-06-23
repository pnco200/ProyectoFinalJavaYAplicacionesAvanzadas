package asis.dto;

import asis.models.Usuario;
import asis.models.Rol;

import java.util.Set;
import java.util.stream.Collectors;

public class UsuarioMapper {

    public static UsuarioDTO toDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.id = usuario.getId();
        dto.mail = usuario.getMail();
        dto.name = usuario.getName();
        dto.is_active = usuario.isIs_active();
        dto.domicilio = usuario.getDomicilio();
        dto.activity = usuario.getActivity();
        dto.extra = usuario.getExtra();
        dto.barrio = usuario.getBarrio() != null ? usuario.getBarrio().getName() : null;
        dto.roles = usuario.getRoles().stream()
                .map(Rol::getNombre)
                .map(Enum::name)
                .collect(Collectors.toSet());
        return dto;
    }

    public static Usuario toEntity(UsuarioInputDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setMail(dto.mail);
        usuario.setPassword(dto.password);
        usuario.setName(dto.name);
        usuario.setIs_active(dto.is_active);
        usuario.setDomicilio(dto.domicilio);
        usuario.setActivity(dto.activity);
        usuario.setExtra(dto.extra);
        return usuario;
    }

    public static void updateEntity(Usuario usuario, UsuarioInputDTO dto) {
        if (dto.mail != null) usuario.setMail(dto.mail);
        if (dto.password != null) usuario.setPassword(dto.password);
        if (dto.name != null) usuario.setName(dto.name);
        usuario.setIs_active(dto.is_active);
        if (dto.domicilio != null) usuario.setDomicilio(dto.domicilio);
        if (dto.activity != null) usuario.setActivity(dto.activity);
        if (dto.extra != null) usuario.setExtra(dto.extra);
    }
}
