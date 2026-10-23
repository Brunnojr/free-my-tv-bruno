# V2.0 — prévias Android TV

Branch: `feature/v2-home`. Base preservada: `backup/v1-funcional`.
Esta prévia ainda depende de revisão visual e de validação de desempenho em GPU real.
Não houve push nem instalação ou comandos ADB na TCL.

## Implementado

- Coleção de até 12 recomendações por aba, mantendo um destaque grande e dois cards menores.
- Direita/esquerda troca o destaque; a navegação para nas extremidades.
- Prateleira continua com o foco inicial. Cima acessa recomendações; cima novamente acessa abas.
- Metadados, nota TMDB, sinopse de até três linhas e serviços por assinatura disponíveis na região configurada.
- Textos preparados quando o conteúdo muda. Rede e preparação de imagens continuam na fila de trabalho existente.
- Cache de imagens limitado a cinco objetos `Backdrop` no visual de cards, contra dez na V1. Cada objeto contém variantes da imagem; esse limite não significa cinco bitmaps no total.
- OK abre um deep link de detalhes do Stremio com IMDb ID e `autoPlay=false`.
- Mensagens traduzidas para aplicativo ausente e identificador indisponível.
- Logo do YouTube reaproveitado de `web/icons/youtube.png`, com variante de contraste produzida pelo código existente de logos.
- Relógio e cotações preservados; removido o título “Para você”. O clima aparece apenas na área inferior.
- Previsão semanal contextual: na prateleira, baixo abre a área inferior; baixo seleciona o resumo de hoje; OK expande a semana, somente para consulta. OK ou Voltar fecha a expansão.
- Uma única consulta Open-Meteo fornece sete dias e 48 horas, com o intervalo de atualização anterior.

`HomeMode.java`, watchdog, debloat, configuração do launcher da TCL e chave de assinatura não foram alterados.
Esta implementação está restrita ao APK Android; os aplicativos webOS/Tizen continuam na versão anterior.

## Configuração local

Foi corrigido apenas o sinal da latitude em `config.properties`: `29.6783` → `-29.6783`, mantendo longitude `-51.1306`, cidade Novo Hamburgo e Celsius.
O arquivo continua ignorado pelo Git. Configurações salvas no próprio aplicativo têm prioridade sobre os valores do build.
A alteração preexistente em `config.example.properties` foi mantida sem edição nesta implementação.

## Validação executada

- `./build.sh`: concluído com `Built FreeMyTV.apk`.
- `tools/translate.py --check`: todos os catálogos correspondem às 181 chaves.
- `git diff --check`: sem erros.
- `apksigner verify --print-certs`: V1 reconstruída e V2 usam o mesmo certificado local.
- Nenhum `config.properties`, keystore ou APK é rastreado pelo Git.
- Instalação e execução em emulador Android TV API 30, 1920×1080, 2 GB, WHPX e SwiftShader.
- Dados reais do TMDB e Open-Meteo carregaram; screenshots foram capturadas.
- Navegação para direita/esquerda, fim da coleção, volta à prateleira e previsão contextual exercitadas.
- Com Stremio desabilitado no emulador, OK não derrubou a Home.
- Com Wi-Fi do emulador desligado, a Home reabriu e a previsão em cache continuou disponível; Wi-Fi foi reativado após o teste.

O emulador contém um aplicativo mínimo de teste com o pacote Netflix para exercitar o filtro por serviços instalados. Seu ícone de play não representa o aplicativo real da Netflix. Não há conta pessoal nos aplicativos do emulador.

## Comparação da prévia 1 — não é um benchmark da TCL

| Medida | V1 | V2 |
| --- | --- | --- |
| APK, bytes | 1.295.060 | 1.307.418 |
| Abertura fria observada | 754 ms | 689–817 ms |
| PSS observado após carregar a Home | 63.301 KB | 55.752 KB |
| Limite do cache de Backdrops no visual de cards | 10 | 5 |
| Mediana de frame, sequência curta de setas | 22 ms | 26 ms |
| Percentil 95 de frame na mesma sequência | 25 ms | 57 ms |

As amostras são curtas, com renderização de software. Na V2, setas também trocam imagens e dados; na V1 movem o foco entre três cards. O ensaio mostrou frames mais lentos na V2, portanto ainda não comprova a fluidez desejada nem autoriza afirmar 60 fps. Medição mais controlada e ajustes de renderização seguem pendentes antes do APK final.

## Limitações e próximos passos

1. Aprovação visual das capturas da Home e da previsão semanal.
2. Validar desempenho em renderização por GPU e depois, com autorização explícita, na TCL.
3. O APK oficial Stremio Android TV 1.10.4 x86 reconheceu o protocolo, mas caiu ao iniciar por `UnsatisfiedLinkError` em `libstremio_core_kotlin.so` (`__atomic_compare_exchange`). A abertura efetiva da página de filme/série ainda não foi validada no Stremio funcional. A Home não consegue interceptar falhas internas de outro aplicativo.
4. A lista de serviços mostra assinaturas (`flatrate`); aluguel e compra não aparecem nesta primeira prévia.
5. Capturas dos temas claro e escuro foram inspecionadas. A revisão completa com texto ampliado ainda está pendente.
6. Não há esportes, trailers, autoplay, integração de contas nem histórico do Stremio.

## Fontes técnicas consultadas

- [Deep links oficiais do Stremio](https://github.com/Stremio/stremio-addon-sdk/blob/master/docs/deep-links.md)
- [Identificadores externos de séries no TMDB](https://developer.themoviedb.org/reference/tv-series-external-ids)
- [Parâmetros de previsão Open-Meteo](https://open-meteo.com/en/docs)
- [Mapa municipal de Novo Hamburgo — IBGE](https://geoftp.ibge.gov.br/cartas_e_mapas/mapas_municipais/estimativas_populacionais/2020/RS/novo_hamburgo_v1.pdf)

## Artefatos locais

Arquivos privados de teste, SDK virtual e capturas ficam em `.local-backup/`, ignorado pelo Git.
A referência Git preserva o código da V1. O APK da V1 foi reconstruído dessa referência com a chave local; ele não é uma cópia extraída da TV.
O APK atual é `2.0-preview.4`, destinado à revisão no emulador. O APK final para a TCL só será preparado após a aprovação visual e as validações pendentes.

## Prévia 2 — ajustes da revisão visual

- Banner começa mais abaixo, com espaço para a legenda de foco dos controles superiores, e ganhou altura. As variantes de imagem foram redimensionadas para manter a proporção dos rostos e cenários.
- Removido o clima repetido abaixo dos banners.
- Fundo menos opaco, prateleira e logos sobre vidro translúcido. Android 8–12 reutilizam os bitmaps já disponíveis para o efeito; Android 13+ mantém o shader existente. Não foram adicionadas ondas animadas nem blur em tempo real.
- Metadados em badges e avaliação em dourado.
- Planos e canais de assinatura são agrupados pelo nome curto da marca, sem duplicatas. Marcas específicas de canais são preservadas: Paramount+ Amazon Channel vira Paramount+, não Prime Video.
- Logos locais de Netflix, HBO Max e Prime Video reaproveitados do projeto. Outros serviços usam o nome curto, sem inventar um logo.
- Clima: hoje à esquerda e sete dias à direita. O card cresce levemente, ganha borda de foco e destaca o dia selecionado.
- OK apresenta a data escolhida, condição, máxima/mínima, chance de chuva, vento máximo e nascer/pôr do sol quando disponíveis. Os dados adicionais vêm da mesma consulta e respeitam o cache existente.
- Ícones meteorológicos vetoriais coloridos, desenhados com círculos, traços e caminhos em Canvas. Sem dependências ou imagens baixadas.

Validações desta revisão: build, assinatura, traduções, teste de normalização de marcas e duplicatas, navegação pelos dias, abertura e retorno dos detalhes, inspeção de capturas claras/escuras e ausência de exceções da Home no log do emulador.
As medições da tabela anterior pertencem à prévia 1 e não foram usadas para afirmar desempenho desta revisão.

Capturas atuais: `.local-backup/v2-preview2-home.png`, `.local-backup/v2-preview2-week.png` e `.local-backup/v2-preview2-day.png`.

## Prévia 3 — fundo discreto e semana recolhida

- Restaurados os valores anteriores de opacidade do fundo e das imagens do vidro, nos temas claro e escuro.
- Blur um pouco maior, calculado junto com os backdrops na fila existente, sem processamento de blur por frame.
- A área inferior volta a mostrar temperatura atual, condição, máxima/mínima, próximas horas e nascer/pôr do sol.
- OK sobre o clima expande sete dias; setas selecionam um dia e OK mantém o acesso aos seus detalhes. Voltar retorna à semana e depois ao resumo.
- Mantidos os ícones coloridos e o crescimento/borda de foco do card do clima.
- Build, traduções e assinatura verificados. O fluxo resumo → semana → sábado → detalhes foi exercitado no emulador e capturado.

Capturas desta revisão: `.local-backup/v2-preview3-home.png`, `.local-backup/v2-preview3-today.png`, `.local-backup/v2-preview3-week.png` e `.local-backup/v2-preview3-day.png`.

## Prévia 4 — foco consistente e uma única expansão do clima

- Foco do clima segue os outros cards: ampliação de 7%, sombra e fundo branco com texto escuro. Removida a borda azul.
- Resumo de hoje mantido. OK expande uma semana somente para leitura, sem cartões de dias selecionáveis nem segunda janela de detalhes.
- Cada dia mostra data, ícone colorido, máxima, mínima e probabilidade de chuva. Dados ausentes aparecem como travessão, sem inventar valores.
- Data ao lado da hora usa dia da semana e `dd/MM`, por exemplo `Quinta-feira, 22/10`, preparada apenas quando o relógio atualiza.
- Build, traduções, certificado e navegação da expansão validados no emulador. Sem erros da Home no log consultado.

Capturas atuais: `.local-backup/v2-preview4-home.png`, `.local-backup/v2-preview4-focus.png` e `.local-backup/v2-preview4-week.png`.

## Ajustes de clima e indicadores brasileiros

- Espaço entre clima e finanças ampliado para acomodar o foco de 7%.
- Semana com bordas suaves e altura calculada pelo conteúdo; rótulo em português: `Prob. de chuva`.
- Hora do topo ampliada de 36 para 48 unidades de layout.
- Cotações em BRL incluem meta Selic (% ao ano, SGS 432) e IPCA acumulado em 12 meses (SGS 13522), via API pública do BCB. A coluna à direita mostra a referência: dia/mês na Selic, mês/ano no IPCA.
- Apenas o último registro de cada série é solicitado, sem chave. Cache persistente de 24 horas, tentativas independentes e intervalo de 30 minutos após falha. Consulta na fila de I/O existente, depois de preços e clima; números anteriores permanecem disponíveis offline.
- Layout comporta até quatro moedas mais os dois indicadores. Sem alterar os indicadores compactos do topo ou o screensaver.
- Build, 183 traduções e certificado de assinatura conferidos. Valores recebidos pela API e renderização conferidos no emulador; cache preservado após encerrar/reabrir o app com Wi-Fi do emulador desativado, depois reativado.
- Capturas: `.local-backup/indicators.png`, `.local-backup/indicators-offline.png` e `.local-backup/rain-short.png`. TCL não alterada.

## Home sem cotações no topo e indicadores opcionais

- Cotações removidas do topo da Home, inclusive quando não há recomendações; continuam no card inferior.
- Banners começam em 214 e têm altura 554 (antes 242/526), mantendo a base e o espaço dos rótulos superiores. Imagens ajustadas à nova altura de foco.
- Selic e IPCA têm opções individuais persistentes em Ajustes do Início; o widget geral de cotações também controla ambos. Indicadores desativados não geram novas consultas ao BCB.
- Ícones nativos: % para juros, gráfico para inflação, $ para dólar. O dólar mantém a fonte atual de cotação, USDT/BRL.
- Configurações acomodam as 13 opções, com animação e áreas de acessibilidade ajustadas à altura das linhas. Corrigido o limite antigo de 12 animações que causou exceção no primeiro teste desta revisão.
- Build, traduções, certificado e diff conferidos. No emulador: esconder Selic e reabrir o app preservou a escolha; esconder IPCA manteve Selic visível. Ambos foram reativados ao terminar.
- Capturas: `.local-backup/banner-taller.png`, `.local-backup/selic-hidden.png`, `.local-backup/ipca-hidden.png`, `.local-backup/indicator-icons.png`.

## Fechamento da V2

- Versão Android `2.0`, versionCode `6`, com a mesma assinatura das prévias e da versão funcional.
- V2 aprovada pelo usuário após os ajustes no emulador. Marco de código: tag `v2.0`; próximos trabalhos na branch `feature/v3`.
- Arquivo de exemplo sem credenciais; configuração real, APKs, chave privada e ambiente local continuam ignorados.
- Permanecem as limitações de validação já documentadas: desempenho na TCL e abertura real do Stremio precisam ser conferidos no aparelho. Fechar esta versão não instala nem altera a TV.
