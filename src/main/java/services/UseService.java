package services;

import java.sql.SQLException;
import java.util.List;

public interface UseService<T> {

    public void ajouter(T t);
    public void  supprimer(T t);
    public void modifier(T t);
    public void block(T t);
    public void unblock(T t);
    public void switchToUser(T t);
    public void switchToAdmin(T t);
    public List<T> afficher() throws SQLException;
}
