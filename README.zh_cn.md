Autotest
-----

[In English Version](README.md)

`Autotest` 为当前基于Java实现的企业级应用提供了一个简单的自动化测试框架, 基于模板方法设计并支持yaml文件格式.

<p align="center"><img src="https://github.com/ericmoshare/autotest/blob/master/src/main/resources/screenshots/demo1.gif?raw=true"></p>


## 介绍

你可以使用maven引入依赖包

```xml
<dependency>
    <groupId>com.github.ericmoshare</groupId>
    <artifactId>autotest</artifactId>
    <version>1.0.2</version>
</dependency>

```

## 为啥选 Autotest?

你可以通过简单地继承 `AbstractAutoTest`, 来实现你的自动化测试功能, 哪怕有众多的场景.

举个例子, 

```java
public class SimpleAutoTest extends AbstractAutoTest {
    
        @Override
        protected InputStream getResourceAsStream() throws RuntimeException {
            return this.getClass().getClassLoader().getResourceAsStream("application.properties");
        }
    
        @Override
        protected void given(Map param) throws RuntimeException {
        }
    
        @Override
        protected void when(Map param) throws RuntimeException {
        }
   
        @Override
        protected Class getSubClass() {
            return this.getClass();
        }
}
```


## 怎么使用?

你可以创建一个 .yaml 格式的文件, 使用跟 `Java` 测试类相同的名字

```yaml
#以下是示例yaml结构
-   name: '[正例]10+20=30'
    error: null
    data:
    -  '10'
    -  '20'
    expected:
        sum: '30'
-   name: '[正例]10+35=45'
    error: null
    data:
    -  '10'
    -  '35'
    expected:
        sum: '45'


```


### 重写方法 `getResourceAsStream`

`Autotest` 提供了一个`Spring` `jdbcTemplate`, 他能实现你对 `dataSource` 的所有 `Fantasy`

举个例子, 

```sql
    clean("person"); //tableName
```

重写方法 `getResourceAsStream()` 时应当返回一个填写了数据库信息的文件输入流 `inputStream`.

举个例子, 

application.properties
```properties
database.url=jdbc:mysql://127.0.0.1:3306/pangu?characterEncoding=UTF8&useSSL=false
database.username=root
database.password=12345678
database.driverClassName=com.mysql.jdbc.Driver
```

Java

```java
    @Override
    protected InputStream getResourceAsStream() throws RuntimeException {
        return this.getClass().getClassLoader().getResourceAsStream("application.properties");
    }

```

**注意**, 如果 不需要用到数据库操作, 可以不配置 application.properties


### `given`

通过方法 `given()` , 你可以做一些预操作, 比如清空数据库, 清空表, 插入数据, 更新数据等等...

举个例子, 

```java
    @Override
    protected void given(Map param) throws RuntimeException {
        clean("person");
        clean("auth_role");
    }

```


### `when`

通过方法 `when`, 你可以执行你的测试代码.


### `expect`
通过重写方法  `expect` , 你可以暂存你测试代码的运行结果, 如果你需要核对/校验测试结果的话.

举个例子, 

```java
    @Override
    protected Map expect(Map param) throws RuntimeException {
        List list = (List) param.get("data");

        Map map = new HashMap();

        int sum = 0;
        for (Object index : list) {
            sum += Integer.valueOf(String.valueOf(index));
        }

        map.put("sum", sum);
        return map;
    }

```


### `error`

如果你期望测试运行时报错, 那么你可以直接把 `error` `message` 添加到 `yaml` 文件 .

举个例子, 

```yaml
-   name: '导入失败-参数错误[txAmount]'
    error: '参数校验错误：txAmount[0]->txAmount:应该大于等于1;'
    data:
    -   accDate: '20180602'
        appCode: xxx
        checkBatchId: '123'
        traceId: '1529045360051'
        txAmount: '0'
        txDate: '20180602'
        txStatus: success
-   name: '导入失败-参数错误'
    error: 'appCode=xxx的表不存在'
    data:
    -   accDate: '20180602'
        appCode: xxx
        checkBatchId: '123'
        traceId: '1529045360051'
        txAmount: '12'
        txDate: '20180602'
        txStatus: success

```



<br/>


## 欢迎各位姥爷打钱


