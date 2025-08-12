package asis.dao;

import asis.models.Renglon;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class RenglonDAO implements IRenglonDAO {

    @Inject
    private EntityManager em;

    @Override
    public Renglon save(Renglon renglon) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(renglon);
            tx.commit();
            return renglon;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Renglon findById(Long id) {
        return em.find(Renglon.class, id);
    }

    @Override
    public List<Renglon> findAll() {
        TypedQuery<Renglon> query = em.createQuery("SELECT r FROM Renglon r", Renglon.class);
        return query.getResultList();
    }

    @Override
    public List<Renglon> findByEncuestaId(Long encuestaId) {
        TypedQuery<Renglon> query = em.createQuery(
            "SELECT r FROM Renglon r WHERE r.encuesta.id = :encuestaId", Renglon.class);
        query.setParameter("encuestaId", encuestaId);
        return query.getResultList();
    }

    @Override
    public List<Renglon> findByTipo(String tipo) {
        TypedQuery<Renglon> query = em.createQuery(
            "SELECT r FROM Renglon r WHERE r.tipo = :tipo", Renglon.class);
        query.setParameter("tipo", tipo);
        return query.getResultList();
    }

    @Override
    public Renglon update(Renglon renglon) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Renglon merged = em.merge(renglon);
            tx.commit();
            return merged;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void delete(Renglon renglon) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (!em.contains(renglon)) {
                renglon = em.merge(renglon);
            }
            em.remove(renglon);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(Long id) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Renglon renglon = em.find(Renglon.class, id);
            if (renglon != null) {
                em.remove(renglon);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id) != null;
    }

    @Override
    public List<Object[]> findDistinctTipos() {
        String sql = """
            SELECT DISTINCT r.tipo, 'VIVIENDA' AS tipo_encuesta
            FROM Renglon r
            INNER JOIN Encuesta e ON e.id = r.encuesta_id
            WHERE e.vivienda_id IS NULL
            UNION
            SELECT DISTINCT r.tipo, 'PERSONA' AS tipo_encuesta
            FROM Renglon r
            INNER JOIN Encuesta e ON e.id = r.encuesta_id
            WHERE e.vivienda_id IS NOT NULL
        """;

        Query query = em.createNativeQuery(sql);
        return query.getResultList(); // each Object[] -> { tipo, tipo_encuesta }
    }

    @Override
    public List<String> findDistinctValoresByTipo(String tipo) {
        TypedQuery<String> query = em.createQuery("SELECT DISTINCT r.valor FROM Renglon r WHERE r.tipo = :tipo", String.class);
        query.setParameter("tipo", tipo);
        return query.getResultList();
    }
    
    
    public List<Object[]> findEncuestasByFiltros(
            List<Map<String, String>> filtrosVivienda,
            List<Map<String, String>> filtrosPersona) {

        StringBuilder sql = new StringBuilder();

        boolean hasVivienda = filtrosVivienda != null && !filtrosVivienda.isEmpty();
        boolean hasPersona = filtrosPersona != null && !filtrosPersona.isEmpty();

        if (hasVivienda && hasPersona) {
            // =============================
            // BOTH FILTER ARRAYS RECEIVED
            // =============================
            sql.append("WITH filtros_vivienda AS (\n");
            appendFiltrosCTE(sql, filtrosVivienda);
            sql.append("),\n");

            sql.append("filtros_persona AS (\n");
            appendFiltrosCTE(sql, filtrosPersona);
            sql.append("),\n");

            sql.append("""
                cte_viviendas_filtradas AS (
                    SELECT e.id AS vivienda_id
                    FROM Encuesta e
                    INNER JOIN Renglon r ON e.id = r.encuesta_id
                    INNER JOIN filtros_vivienda f ON f.tipo = r.tipo AND f.valor = r.valor
                    GROUP BY e.id
                    HAVING COUNT(*) = (SELECT COUNT(*) FROM filtros_vivienda)
                ),
                cte_personas_filtradas AS (
                    SELECT ep.id AS persona_id, ep.vivienda_id AS vivienda_id
                    FROM Encuesta ep
                    INNER JOIN Renglon r ON ep.id = r.encuesta_id
                    INNER JOIN filtros_persona f ON f.tipo = r.tipo AND f.valor = r.valor
                    GROUP BY ep.id, ep.vivienda_id
                    HAVING COUNT(*) = (SELECT COUNT(*) FROM filtros_persona)
                ),
                cte_viviendas_finales AS (
                    SELECT v.vivienda_id
                    FROM cte_viviendas_filtradas v
                    INNER JOIN cte_personas_filtradas p ON v.vivienda_id = p.vivienda_id
                ),
                cte_final_encuestas AS (
                    SELECT e.*
                    FROM Encuesta e
                    WHERE e.id IN (SELECT vivienda_id FROM cte_viviendas_finales)
                    UNION ALL
                    SELECT ep.*
                    FROM Encuesta ep
                    WHERE ep.id IN (
                        SELECT persona_id
                        FROM cte_personas_filtradas
                        WHERE vivienda_id IN (SELECT vivienda_id FROM cte_viviendas_finales)
                    )
                )
                SELECT 
                    e.id AS encuesta_id,
                    e.tipo,
                    e.vivienda_id,
                    JSON_ARRAYAGG(JSON_OBJECT('tipo', r.tipo, 'valor', r.valor)) AS respuestas_json
                FROM cte_final_encuestas e
                LEFT JOIN Renglon r ON r.encuesta_id = e.id
                GROUP BY e.id, e.tipo, e.vivienda_id
            """);

        } else {
            // =============================
            // ONLY ONE FILTER ARRAY
            // =============================
            List<Map<String, String>> filtros = hasVivienda ? filtrosVivienda : filtrosPersona;

            sql.append("WITH filtros AS (\n");
            appendFiltrosCTE(sql, filtros);
            sql.append("),\n");

            sql.append("""
                matches AS (
                    SELECT r.encuesta_id
                    FROM Renglon r
                    JOIN filtros f ON r.tipo = f.tipo AND r.valor = f.valor
                    GROUP BY r.encuesta_id
                    HAVING COUNT(DISTINCT CONCAT(r.tipo, '-', r.valor)) = (SELECT COUNT(*) FROM filtros)
                )
                SELECT 
                    e.id AS encuesta_id,
                    e.tipo,
                    JSON_ARRAYAGG(JSON_OBJECT('tipo', r.tipo, 'valor', r.valor)) AS respuestas_json
                FROM Encuesta e
                JOIN Renglon r ON r.encuesta_id = e.id
                WHERE e.id IN (SELECT encuesta_id FROM matches)
                GROUP BY e.id, e.tipo
                ORDER BY e.id
            """);
        }

        Query query = em.createNativeQuery(sql.toString());
        return query.getResultList();
    }

    /**
     * Appends the CTE SELECT ... UNION ALL ... for a filter list
     */
    private void appendFiltrosCTE(StringBuilder sql, List<Map<String, String>> filtros) {
        for (int i = 0; i < filtros.size(); i++) {
            Map<String, String> filtro = filtros.get(i);
            sql.append("SELECT '")
               .append(filtro.get("tipo").replace("'", "''"))
               .append("' AS tipo, '")
               .append(filtro.get("valor").replace("'", "''"))
               .append("' AS valor");
            if (i < filtros.size() - 1) {
                sql.append("\nUNION ALL\n");
            } else {
                sql.append("\n");
            }
        }
    }
}
