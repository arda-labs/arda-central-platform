package vn.io.arda.central.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.io.arda.central.domain.entity.Menu;

import java.util.List;
import java.util.UUID;

@Repository
public interface MenuRepository extends JpaRepository<Menu, UUID> {

    /**
     * All root menus (no parent), ordered by sortOrder.
     */
    @EntityGraph(attributePaths = {"children", "children.children"})
    @Query("SELECT m FROM Menu m WHERE m.parent IS NULL ORDER BY m.sortOrder ASC")
    List<Menu> findAllRootMenus();
}
