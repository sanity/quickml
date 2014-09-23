package quickml.supervised.collaborativeFiltering;

import java.io.Serializable;

/**
 * Created by ian on 8/16/14.
 */
public class UserItem implements Serializable {
    private static final long serialVersionUID = -5759815197196667292L;
    private long user, item;

    public UserItem(final long user, final long item) {

        this.user = user;
        this.item = item;
    }

    public long getUser() {
        return user;
    }

    public long getItem() {
        return item;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserItem{");
        sb.append("user=").append(user);
        sb.append(", item=").append(item);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final UserItem userItem = (UserItem) o;

        if (item != userItem.item) return false;
        if (user != userItem.user) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (user ^ (user >>> 32));
        result = 31 * result + (int) (item ^ (item >>> 32));
        return result;
    }
}
