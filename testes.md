O que testar?
=============

Testes Unitários:
-----------------
 - **Parser do XML** (`XMLFeedParser`)
Resolvemos testar o Parser o XML, por ser parte integral do funcionamento do app. Caso o parser faça a leitura do XML de maneira correta, a aplicação não terá nenhum conteúdo para mostrar.

Como precisamos de um objeto do tipo `XmlPullParser` para passar como parâmetro às nossas funções que serão testadas, e não desejamos testar o funcionamento de um `XmlPullParser` de verdade, visto que é uma classe do Android, criamos um mock que será retornado por um método auxiliar:

    private static XmlPullParser mockParser() throws XmlPullParserException {
        XmlPullParserFactory factory = mock(XmlPullParserFactory.class);
        XmlPullParser xpp = mock(XmlPullParser.class);
        when(factory.newPullParser()).thenReturn(xpp);
        return xpp;
    }

 1.  Leitura do valor textual de uma tag (`readText`)
	O método `readText` é responsável por retornar o valor textual da tag atual, então queremos testar se dada uma entrada, o resultado é o texto esperado:

    @Test
    public void readText_returnsExpectedText() throws XmlPullParserException, IOException {
        String expected = "Texto teste";
    	XmlPullParser xpp = mockParser();
    	when(xpp.next()).thenReturn(XmlPullParser.TEXT);
    	when(xpp.getText()).thenReturn(expected);
    	String actual = XmlFeedParser.readText(xpp);
    	assertEquals(expected, actual);
    }

	Definimos o comportamento do mock parser para ser análogo ao de um parser verdadeiro quando no método `readText`, definindo que o próximo elemento seria textual, e que ao pegar esse elemento, retornaria a String esperada. 
	
 2.  Leitura de uma tag de um Item (`readData`)
 3.  Leitura da tag *enclosure* (`readEnclosure`)

Vamos testar se a função `readEnclosure` consegue com sucesso ler a tag `<enclosure>` proveniente de um arquivo XML, retornando atributo "url" onde consta o link para download do podcast.


Testes Integração:
-----------------

 - **Acesso ao Banco de Dados** `(PodcastProvider`/ `PodcastDBHelper`)
 1.  Inserção de podcast no banco
 2. 
 2. Atualização dos dados de um podcast salvo
 3. Pegar podcasts do banco
 4. Deletar um podcast do banco