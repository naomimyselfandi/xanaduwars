package io.github.naomimyselfandi.xanaduwars.gameplay.runtime;

/*
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ActionExecutorImplFollowUpTest {

    // This class is mostly here to fix a false negative in the coverage report.

    @Mock
    private Unit target;

    @Mock
    private Action<Tile, Unit> action;

    @Mock
    private GameState gameState;

    @BeforeEach
    void setup() {
        when(gameState.evaluate(any(Validation.class))).thenReturn(true);
        when(gameState.evaluate(any(Validation.class), Mockito.<Consumer<Rule<?, ?>>>any())).thenReturn(true);
    }

    @ParameterizedTest
    @ValueSource(strings = {"PLAYER", "TILE", "UNIT"})
    void execute(String kind) {
        var element = switch (kind) {
            case "PLAYER" -> mock(Player.class);
            case "TILE" -> mock(Tile.class);
            case "UNIT" -> mock(Unit.class);
            default -> Assertions.<Element>fail();
        };
        when(element.gameState()).thenReturn(gameState);
        assertThatCode(() -> new ActionExecutorImpl().execute(List.of(), element)).doesNotThrowAnyException();
        if (element instanceof Unit unit) {
            verify(unit).canAct(false);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void execute() {
        var element = mock(Tile.class);
        when(element.gameState()).thenReturn(gameState);
        var item = new ActionItem<>(action, target);
        when(gameState.evaluate(eq(new StandardTargetValidation<>(action, element, target)), any(Consumer.class)))
                .thenReturn(false);
        try {
            new ActionExecutorImpl().execute(List.of(item), element);
        } catch (ActionException _) {}
    }

}
*/
