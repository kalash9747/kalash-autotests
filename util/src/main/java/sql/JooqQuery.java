package sql;

import encryption.User;
import org.jooq.CloseableDSLContext;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.function.Function;

/**
 * Класс для работы с БД средствами Jooq
 */
public class JooqQuery {
    private final User user;

    public JooqQuery(User user) {
        this.user = user;
    }

    public <T> T executeQuery(Function<DSLContext, T> query) {
        try (CloseableDSLContext context = DSL.using(user.getUrl(), user.getLogin(), user.getPassword())) {
            return query.apply(context);
        }
    }
}
