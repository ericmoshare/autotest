Autotest
-----

[In Chinese 中文版](README.zh_cn.md)


The `Autotest` provides a simple `framework` for modern Java-based enterprise applications - on any kind of deployment platform, 
based on template-method design pattern which supported by `yaml`.

![GitHub][demo]

## Value

The Value of `Autotest` are:

- Keep your code `Robust`<br/>
  Building multiple `Autotest` scenarios, you can examine your services with limitless imagination.
  
- Keep your services `Availabile`<br/>
  Depends on Maven, you can run all your test cases before you deploy.

  
## Dependency

You can use maven, whom might become your `BFF`.

```xml
<dependency>
    <groupId>com.github.ericmoshare</groupId>
    <artifactId>autotest</artifactId>
    <version>1.0.2</version>
</dependency>

```

## Why Autotest?

You can simply extends `AbstractAutoTest` to fullfill your autotest-tasks, which might contains a lot of `Scenarios`.

Such as, 

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


## How to use?

You can create a yaml file with the same name as the Java class, use the structure as

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


### `getResourceAsStream`

`Autotest` provides a `Spring-jdbcTemplate`, which can simply fullfill all your fantasy about dataSource operations.

Such as,

```sql
    clean("person"); //tableName
```

The abstract method `getResourceAsStream()` should return inputStream, which contain dataSource information.

Such as,

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



Be aware, if you dont need that, you can skiped..


### `given`

Through the method `given()` , you can do some preparation like clean table, insert, update, etc...

Such as

```java
    @Override
    protected void given(Map param) throws RuntimeException {
        clean("person");
        clean("auth_role");
    }

```


### `when`

Through the method `when` , you can run your own target test.


### `expect`
Through the method `expect` , you can store the results in case you want to verify the expectation.

Such as,

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
If you expect some error, just add your error message to the yaml file.

Such as,

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


## Thanks
`Davey.wu`'s project  [Test4j][test4j] inspires me a lot.

**Thanks Alibaba Group.**

<br/>

## Thanks for Donation
![Donate with Wechat][donate]


[test4j]: https://github.com/test4j/test4j
[demo]: https://github.com/ericmoshare/autotest/blob/master/src/main/resources/screenshots/demo1.gif
[donate]: https://github.com/ericmoshare/autotest/blob/master/src/main/resources/screenshots/donate.JPG

