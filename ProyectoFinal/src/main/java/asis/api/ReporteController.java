package asis.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;

@Path("/reportes")
@Tag(name = "Reportes", description = "Operaciones de subida y descarga de reportes")
public class ReporteController {

    private final String PUBLIC_DOCS_PATH;
    private final String PRIVATE_DOCS_PATH;

    public ReporteController() {
        String base = System.getenv("REPORTS_DIR");
        if (base == null || base.isBlank()) {
            base = System.getProperty(
                "reports.dir",
                System.getProperty("user.home") + File.separator + "ProyectoFinal" + File.separator + "docs"
            );
        }

        String sep = File.separator;
        this.PUBLIC_DOCS_PATH  = base + sep + "public_docs"  + sep;
        this.PRIVATE_DOCS_PATH = base + sep + "private_docs" + sep;

        new File(PUBLIC_DOCS_PATH).mkdirs();
        new File(PRIVATE_DOCS_PATH).mkdirs();
    }

    @POST
    @Path("/upload/{visibilidad}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(summary = "Subir un reporte PDF", responses = {
            @ApiResponse(responseCode = "200", description = "Reporte subido correctamente"),
            @ApiResponse(responseCode = "400", description = "Error en la petición o archivo no válido"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response uploadReporte(
            @PathParam("visibilidad") @Parameter(description = "Define si el reporte es 'publico' o 'privado'", required = true) String visibilidad,
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDisposition) {

        String uploadPath;
        if ("publico".equalsIgnoreCase(visibilidad)) {
            uploadPath = PUBLIC_DOCS_PATH;
        } else if ("privado".equalsIgnoreCase(visibilidad)) {
            uploadPath = PRIVATE_DOCS_PATH;
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"La visibilidad debe ser 'publico' o 'privado'\"}").build();
        }

        try {
            // Buscar el último número usado en la carpeta
            File folder = new File(uploadPath);
            int nextNumber = getNextFileNumber(folder);

            // Crear nombre con prefijo autoincremental
            String fileName = nextNumber + "_" + fileDisposition.getFileName();
            File file = new File(uploadPath + fileName);

            // Guardar archivo
            try (FileOutputStream fos = new FileOutputStream(file)) {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = fileInputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                }
            }

            return Response.ok("{\"message\": \"Reporte subido exitosamente\", \"filename\": \"" + file.getName() + "\"}").build();

        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error al guardar el archivo: " + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/descargar/{visibilidad}/{filename}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Operation(summary = "Descargar un reporte PDF", responses = {
            @ApiResponse(responseCode = "200", description = "Reporte descargado correctamente"),
            @ApiResponse(responseCode = "404", description = "Reporte no encontrado")
    })
    public Response downloadReporte(
            @PathParam("visibilidad") String visibilidad,
            @PathParam("filename") String filename) {

        String filePath;
        if ("publico".equalsIgnoreCase(visibilidad)) {
            filePath = PUBLIC_DOCS_PATH + filename;
        } else if ("privado".equalsIgnoreCase(visibilidad)) {
            filePath = PRIVATE_DOCS_PATH + filename;
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"La visibilidad debe ser 'publico' o 'privado'\"}").build();
        }

        File file = new File(filePath);
        if (!file.exists()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Reporte no encontrado\"}").build();
        }

        return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"")
                .build();
    }
    @GET
    @Path("/listar/{visibilidad}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Listar todos los reportes según visibilidad", responses = {
            @ApiResponse(responseCode = "200", description = "Listado de reportes obtenido correctamente"),
            @ApiResponse(responseCode = "400", description = "Visibilidad inválida")
    })
    public Response listarReportes(
            @PathParam("visibilidad") @Parameter(description = "Define si los reportes son 'publico' o 'privado'", required = true) String visibilidad) {

        String listPath;
        if ("publico".equalsIgnoreCase(visibilidad)) {
            listPath = PUBLIC_DOCS_PATH;
        } else if ("privado".equalsIgnoreCase(visibilidad)) {
            listPath = PRIVATE_DOCS_PATH;
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"La visibilidad debe ser 'publico' o 'privado'\"}").build();
        }

        File directory = new File(listPath);
        if (!directory.exists() || !directory.isDirectory()) {
            return Response.ok("[]").build(); // si no hay carpeta, devolvemos lista vacía
        }

        String[] files = directory.list();
        if (files == null) {
            files = new String[0];
        }

        return Response.ok(files).build();
    }
    @DELETE
    @Path("/{visibilidad}/{nombreArchivo}")
    @Operation(summary = "Eliminar un reporte por nombre y visibilidad", responses = {
            @ApiResponse(responseCode = "200", description = "Reporte eliminado correctamente"),
            @ApiResponse(responseCode = "400", description = "Visibilidad inválida"),
            @ApiResponse(responseCode = "404", description = "Archivo no encontrado")
    })
    public Response eliminarReporte(
            @PathParam("visibilidad") @Parameter(description = "Define si el reporte es 'publico' o 'privado'", required = true) String visibilidad,
            @PathParam("nombreArchivo") @Parameter(description = "Nombre del archivo a eliminar", required = true) String nombreArchivo) {

        String basePath;
        if ("publico".equalsIgnoreCase(visibilidad)) {
            basePath = PUBLIC_DOCS_PATH;
        } else if ("privado".equalsIgnoreCase(visibilidad)) {
            basePath = PRIVATE_DOCS_PATH;
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"La visibilidad debe ser 'publico' o 'privado'\"}").build();
        }

        File file = new File(basePath, nombreArchivo);
        if (!file.exists() || !file.isFile()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Archivo no encontrado\"}").build();
        }

        boolean deleted = file.delete();
        if (deleted) {
            return Response.ok("{\"message\": \"Archivo eliminado correctamente\"}").build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"No se pudo eliminar el archivo\"}").build();
        }
    }

    private int getNextFileNumber(File folder) {
        int maxNumber = 0;
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                String name = f.getName();
                // Verifica si el nombre empieza con un número + "_"
                int underscoreIndex = name.indexOf("_");
                if (underscoreIndex > 0) {
                    try {
                        int num = Integer.parseInt(name.substring(0, underscoreIndex));
                        if (num > maxNumber) {
                            maxNumber = num;
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        return maxNumber + 1;
    }
}
