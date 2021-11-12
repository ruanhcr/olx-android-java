# OLX Clone
App feito em curso que utiliza algumas abordagens do app real OLX.
<br>É possível fazer o cadastro de usuário no [Firebase](https://accounts.google.com/signin/v2/identifier?passive=1209600&osid=1&continue=https%3A%2F%2Fconsole.firebase.google.com%2F%3Fhl%3Dpt-br&followup=https%3A%2F%2Fconsole.firebase.google.com%2F%3Fhl%3Dpt-br&hl=pt-br&flowName=GlifWebSignIn&flowEntry=ServiceLogin), assim como Login e também Logout.
<br>O app já apresenta anúncios que foram cadastrados posteriormente no banco de dados na homepage, sem necessariamente o usuário possuir uma conta cadastrada.
<br>Utiliza-se filtros por estados brasileiros e também por categorias através de Spinners.
<br>O usuário logado pode cadastrar um anúncio completo com imagens, categoria, estado, valor, telefone e descrição do produto.
<br>São empregados ainda [Máscara de moeda](https://github.com/BlacKCaT27/CurrencyEditText) e [Máscara de telefone](https://github.com/santalu/maskara) para facilitar o cadastro de anúncios.
<br>Para a exibição das imagens é utilizado a biblioteca [Picasso](https://square.github.io/picasso/).
<br>É necessário configurar um projeto no [Firebase](https://accounts.google.com/signin/v2/identifier?passive=1209600&osid=1&continue=https%3A%2F%2Fconsole.firebase.google.com%2F%3Fhl%3Dpt-br&followup=https%3A%2F%2Fconsole.firebase.google.com%2F%3Fhl%3Dpt-br&hl=pt-br&flowName=GlifWebSignIn&flowEntry=ServiceLogin) e substituir o arquivo google-services.json gerado para que o app utilize o banco de dados.</br>
