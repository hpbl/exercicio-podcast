O que testar?
=============

Testes Unitários:
-----------------
 - **Parser do XML** (`XMLFeedParser`)
Resolvemos testar o Parser o XML, por ser parte integral do funcionamento do app. Caso o parser faça a leitura do XML de maneira correta, a aplicação não terá nenhum conteúdo para mostrar.

Como precisamos de um objeto do tipo `XmlPullParser` para passar como parâmetro às nossas funções que serão testadas, e não desejamos testar o funcionamento de um `XmlPullParser` de verdade, visto que é uma classe do Android, criamos um mock que será retornado por um método auxiliar:

```Java
   private static XmlPullParser mockParser() throws XmlPullParserException {
        XmlPullParserFactory factory = mock(XmlPullParserFactory.class);
        XmlPullParser xpp = mock(XmlPullParser.class);
        when(factory.newPullParser()).thenReturn(xpp);
        return xpp;
    }
```

 1.  Leitura do valor textual de uma tag (`readText`):
 
O método `readText` é responsável por retornar o valor textual da tag atual, então queremos testar se dada uma entrada, o resultado é o texto esperado.

```java
    @Test
    public void readText_returnsExpectedText() throws XmlPullParserException, IOException {
        String expected = "Texto teste";
    	XmlPullParser xpp = mockParser();
    	when(xpp.next()).thenReturn(XmlPullParser.TEXT);
    	when(xpp.getText()).thenReturn(expected);
    	String actual = XmlFeedParser.readText(xpp);
    	assertEquals(expected, actual);
    }
```

Definimos o comportamento do mock parser para ser análogo ao de um parser verdadeiro quando no método `readText`, definindo que o próximo elemento seria textual, e que ao pegar esse elemento, retornaria a String esperada. 

 2.  Leitura de uma tag de um Item (`readData`):

O método `readData` é utilizado para fazer o parse de uma tag completa, utilizando o `readText` para retornar o valor textual daquela da mesma.  Fizemos um teste para cada uma das tags de interesse, para garantir a leitura das informações.

```java
    @Test
    public void readData_returnsCorrectTitle() throws XmlPullParserException, IOException {
        String expected = "Oi tudo bom com você";

        XmlPullParser xpp = mockParser();
        PowerMockito.stub(PowerMockito.method(XmlFeedParser.class, "readText")).toReturn(expected);

        String actual = XmlFeedParser.readData(xpp, "title");
        assertEquals(expected, actual);
    }

    @Test
    public void readData_returnsCorrectGuid() throws XmlPullParserException, IOException {
        String expected = "http://frontdaciencia.ufrgs.br/#1";

        XmlPullParser xpp = mockParser();
        PowerMockito.stub(PowerMockito.method(XmlFeedParser.class, "readText")).toReturn(expected);

        String actual = XmlFeedParser.readData(xpp, "guid");
        assertEquals(expected, actual);
    }

    @Test
    public void readData_returnsCorrectPubDate() throws XmlPullParserException, IOException {
        String expected = "Sun, 20 Jun 2010 10:40:05 GMT";

        XmlPullParser xpp = mockParser();
        PowerMockito.stub(PowerMockito.method(XmlFeedParser.class, "readText")).toReturn(expected);

        String actual = XmlFeedParser.readData(xpp, "pubDate");
        assertEquals(expected, actual);
    }

    @Test
    public void readData_returnsCorrectDescription() throws XmlPullParserException, IOException {
        String expected = "Programa 1";

        XmlPullParser xpp = mockParser();
        PowerMockito.stub(PowerMockito.method(XmlFeedParser.class, "readText")).toReturn(expected);

        String actual = XmlFeedParser.readData(xpp, "description");
        assertEquals(expected, actual);
    }
```
Como o método sendo testado depende de um outro método estático da mesma classe (`readText`), utilizamos PowerMockito para fazer o *stubbing* deste último, para que seu funcionamento não interfira no teste, permitindo que o resultado do teste aponte o comportamento do `readData`.


 3.  Leitura da tag *enclosure* (`readEnclosure`):
 
Diferentemente das outras tags, *enclosure* é self-closing, e por isso necessita de um método próprio. Vamos testar se a função `readEnclosure` consegue ler a tag com sucesso, retornando o valor do atributo "url" onde consta o link para download do podcast.

``` java
    @Test
    public void readEnclosure_returnsCorrectUrl() throws XmlPullParserException, IOException {
        String expected = "https://hpbl.github.io/hub42_APS/audio/Oi%20Tudo%20Bom.mp3";

        XmlPullParser xpp = mockParser();
        when(xpp.getAttributeValue(null, "url")).thenReturn(expected);
        String actual = XmlFeedParser.readEnclosure(xpp);
        assertEquals(expected, actual);
    }
```

![UnitTestsResult](Images/UnitTestsResult.png)

----------


Testes Integração:
-----------------

 - **Acesso ao Banco de Dados** `(PodcastProvider`/ `PodcastDBHelper`)
 1.  Inserção de podcast no banco
 2. 
 2. Atualização dos dados de um podcast salvo
 3. Pegar podcasts do banco
 4. Deletar um podcast do banco



Testes De Interface:
-----------------

Os testes de interface foram realizados usando Espresso, com o intuito de verificar se a interface foi disposta, e reage a ações corretamente.

 1.  O primeiro podcast apresentado é o primeiro do XML?
 Queriamos saber se após fazer o parse do XML, e apresentar os resultados na tela, se foi mantida a ordem dos podcasts, sendo assim o primeiro a ser apresentado o que tem título: Ciência e Pseudociência
 
 ```java
     @Test
    public void firstPodcast_isCiencia() {
        String expected = "Ciência e Pseudociência";

        SystemClock.sleep(35000);

        onData(anything())
                .inAdapterView(withId(R.id.items))
                .atPosition(0)
                .onChildView(withId(R.id.item_title))
                .check(matches(withText(expected)));
    }
```

2. O último podcast está com a data certa?
Queriamos saber se os dados dos podcasts estão condizentes, no caso, se o título do podcast bate com sua data de publicação.

```java
    @Test
    public void lastPodcast_hasRightDate() {
        String title = "Frontdaciência - T08E29 - Mario Bunge I";
        String expectedDate = "Mon, 18 Sep 2017 12:00:00 GMT";

        SystemClock.sleep(35000);

        onData(withItemTitle(title))
                .inAdapterView(withId(R.id.items))
                .onChildView(withId(R.id.item_date))
                .check(matches(withText(expectedDate)));
    }
```

Para conseguir a referência ao item específico da lista, criamos um FeatureMatcher que vai conferir se o item tem o título desejado:

```java
    private static FeatureMatcher<ItemFeed, String> withItemTitle(final String itemTitle) {
        return new FeatureMatcher<ItemFeed, String>(equalTo(itemTitle), "with itemTitle", "itemTitle") {
            @Override
            protected String featureValueOf(ItemFeed actual) {
                return actual.getTitle();
            }
        };
    }
```

