# Guia de Contribuicao

Obrigado por considerar contribuir para o Financial Assembly! Este documento fornece diretrizes para contribuir com o projeto.

## Codigo de Conduta

Ao participar deste projeto, voce concorda em manter um ambiente respeitoso e colaborativo.

## Como Contribuir

### Reportando Bugs

1. Verifique se o bug ja foi reportado nas Issues
2. Se nao, crie uma nova Issue incluindo:
   - Titulo claro e descritivo
   - Descricao detalhada do problema
   - Passos para reproduzir
   - Comportamento esperado vs. obtido
   - Screenshots (se aplicavel)
   - Ambiente (SO, versao do Java/Node, etc.)

### Sugerindo Melhorias

1. Verifique se a sugestao ja existe nas Issues
2. Crie uma nova Issue com:
   - Titulo claro da funcionalidade
   - Descricao detalhada da melhoria
   - Justificativa (por que e util?)
   - Exemplos de uso (se aplicavel)

### Pull Requests

1. Fork o repositorio
2. Crie uma branch a partir de `develop`:
   ```bash
   git checkout -b feature/minha-funcionalidade
   ```

3. Faca suas alteracoes seguindo os padroes:
   - **Backend (Java):**
     - Siga as convencoes de codigo Java
     - Use Lombok apropriadamente
     - Escreva JavaDoc para classes e metodos publicos
     - Mantenha cobertura de testes >= 80%

   - **Frontend (Angular):**
     - Siga o Angular Style Guide
     - Use TypeScript strict mode
     - Escreva testes unitarios
     - Mantenha cobertura de testes >= 80%

4. Execute os testes:
   ```bash
   # Backend
   cd backend && mvn test

   # Frontend
   cd frontend && npm test
   ```

5. Execute o linter:
   ```bash
   # Backend (se configurado)
   cd backend && mvn checkstyle:check

   # Frontend
   cd frontend && npm run lint
   ```

6. Commit suas mudancas usando Conventional Commits:
   ```bash
   git commit -m "feat: adiciona validacao de CPF"
   git commit -m "fix: corrige calculo de juros"
   git commit -m "docs: atualiza README"
   ```

7. Push para sua branch:
   ```bash
   git push origin feature/minha-funcionalidade
   ```

8. Abra um Pull Request para `develop`

### Padrao de Commits

Use [Conventional Commits](https://www.conventionalcommits.org/):

- `feat`: Nova funcionalidade
- `fix`: Correcao de bug
- `docs`: Mudancas na documentacao
- `style`: Formatacao (nao afeta codigo)
- `refactor`: Refatoracao de codigo
- `test`: Adicao ou correcao de testes
- `chore`: Tarefas de manutencao
- `perf`: Melhoria de performance
- `ci`: Mudancas no CI/CD

Exemplos:
```
feat(cliente): adiciona validacao de CNPJ
fix(transacao): corrige calculo de saldo
docs(readme): atualiza instrucoes de instalacao
test(service): adiciona testes para ClienteService
```

## Padroes de Codigo

### Backend (Java/Spring Boot)

```java
/**
 * Service para gerenciar operacoes de clientes.
 */
@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository repository;

    /**
     * Busca um cliente por ID.
     *
     * @param id ID do cliente
     * @return Cliente encontrado
     * @throws ClienteNotFoundException se nao encontrado
     */
    public Cliente findById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ClienteNotFoundException(id));
    }
}
```

### Frontend (Angular/TypeScript)

```typescript
/**
 * Service para gerenciar clientes.
 */
@Injectable({
  providedIn: 'root'
})
export class ClienteService {
  private readonly apiUrl = `${environment.apiUrl}/clientes`;

  constructor(private http: HttpClient) {}

  /**
   * Busca um cliente por ID.
   */
  findById(id: number): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.apiUrl}/${id}`);
  }
}
```

## Estrutura de Branches

- `main` - Producao (somente releases)
- `develop` - Desenvolvimento (branch base)
- `feature/*` - Novas funcionalidades
- `fix/*` - Correcoes de bugs
- `hotfix/*` - Correcoes urgentes em producao

## Processo de Review

1. O PR sera revisado por pelo menos um maintainer
2. Todos os testes devem passar
3. Cobertura de codigo deve ser mantida
4. O codigo deve seguir os padroes estabelecidos
5. Mudancas solicitadas devem ser atendidas

## Licenca

Ao contribuir, voce concorda que suas contribuicoes serao licenciadas sob a Apache License 2.0.

## Duvidas?

Abra uma Issue ou entre em contato com o time via support@financialassembly.com

Obrigado por contribuir!
