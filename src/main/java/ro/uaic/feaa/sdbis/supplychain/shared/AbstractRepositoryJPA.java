package ro.uaic.feaa.sdbis.supplychain.shared;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

/**
 * Biblioteca tehnica comuna (Infrastructure) - Figura 10 din Project_OOAD.docx.
 * Contine accesul la EntityManager pentru implementarea operatiilor CRUD de baza.
 */
@Transactional
public abstract class AbstractRepositoryJPA<T, ID> {

    @PersistenceContext
    protected EntityManager em;

    private final Class<T> entityClass;

    protected AbstractRepositoryJPA(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public void add(T entity) {
        em.persist(entity);
    }

    public void save(T entity) {
        em.merge(entity);
    }

    public void delete(T entity) {
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }

    public Optional<T> findById(ID id) {
        return Optional.ofNullable(em.find(entityClass, id));
    }

    public List<T> findAll() {
        return em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass)
                 .getResultList();
    }
}
