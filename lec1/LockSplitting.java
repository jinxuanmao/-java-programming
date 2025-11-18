import java.util.*;
import java.util.regex.Pattern;

/**
 * 演示减少锁的持有时间
 */
class BetterAttributeStore {
    // 使用 this 作为锁来保护 attributes
    @GuardedBy("this")
    private final Map<String, String> attributes = new HashMap<>();

    /**
     * 通过用户名查地点并用正则匹配
     * 锁的范围被缩小到只包含读取共享变量部分
     */
    public boolean userLocationMatches(String name, String regexp) {
        String key = "users." + name + ".location";
        String location;

        // 缩小同步代码块范围：只保护共享变量访问
        synchronized (this) {
            location = attributes.get(key);
        }

        // 后续操作无需持有锁
        if (location == null) {
            return false;
        }
        return Pattern.matches(regexp, location);
    }

    // 写入方法，用来在测试中初始化数据
    public void setUserLocation(String name, String location) {
        synchronized (this) {
            attributes.put("users." + name + ".location", location);
        }
    }
}



/**
 * 演示锁分解（Lock Splitting）
 * 将用户集合和查询集合使用不同的锁保护，提高并发性能
 */
class ServerStatusAfterSplit {

    // 使用不同的对象作为锁
    @GuardedBy("users")
    private final Set<String> users;

    @GuardedBy("queries")
    private final Set<String> queries;

    public ServerStatusAfterSplit() {
        this.users = new HashSet<>();
        this.queries = new HashSet<>();
    }

    public void addUser(String u) {
        synchronized (users) {
            users.add(u);
        }
    }

    public void addQuery(String q) {
        synchronized (queries) {
            queries.add(q);
        }
    }

    public Set<String> getUsersSnapshot() {
        synchronized (users) {
            return new HashSet<>(users);
        }
    }

    public Set<String> getQueriesSnapshot() {
        synchronized (queries) {
            return new HashSet<>(queries);
        }
    }
}


/**
 * 测试类 —— 让两段代码都能运行并看到结果
 */
public class ConcurrencyTest {
    public static void main(String[] args) {

        System.out.println("===== 测试：减少锁的持有时间 =====");
        BetterAttributeStore store = new BetterAttributeStore();
        store.setUserLocation("zhangsan", "Beijing");

        System.out.println("匹配 'Beijing'? " +
                store.userLocationMatches("zhangsan", "Beijing"));

        System.out.println("匹配 'Shanghai'? " +
                store.userLocationMatches("zhangsan", "Shanghai"));


        System.out.println("\n===== 测试：锁分解（Lock Splitting） =====");
        ServerStatusAfterSplit server = new ServerStatusAfterSplit();

        // 模拟多线程情况下 adds
        server.addUser("userA");
        server.addUser("userB");

        server.addQuery("select * from t1");
        server.addQuery("update t2 set name='abc'");

        System.out.println("用户集合：" + server.getUsersSnapshot());
        System.out.println("查询集合：" + server.getQueriesSnapshot());
    }
}
