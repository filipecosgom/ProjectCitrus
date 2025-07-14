import { render, screen, act } from "@testing-library/react";
import NotFound404 from "./404NotFound";

// Mock useTranslation
jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key, opts) => {
      if (key === "notfoundTitle") return "Página não encontrada";
      if (key === "notfoundPhrase") return "A página que procura não existe.";
      if (key === "notfoundRedirect")
        return `Redirecionando em ${opts.segundos} segundos...`;
      return key;
    },
  }),
}));

// Mock useNavigate
const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => {
  const originalModule = jest.requireActual("react-router-dom");
  return {
    ...originalModule,
    useNavigate: () => mockNavigate,
  };
});

describe("NotFound404", () => {
    let MemoryRouter;
    beforeEach(() => {
        MemoryRouter = require("react-router-dom").MemoryRouter;
        mockNavigate.mockClear();
    });

    test("renderiza todos os elementos principais", () => {
        render(
            <MemoryRouter>
                <NotFound404 />
            </MemoryRouter>
        );
        expect(screen.getByText("Página não encontrada")).toBeInTheDocument();
        expect(
            screen.getByText("A página que procura não existe.")
        ).toBeInTheDocument();
        expect(
            screen.getByText(/Redirecionando em 10 segundos/)
        ).toBeInTheDocument();
        expect(screen.getByAltText("404")).toBeInTheDocument();
    });

    test("faz redirect imediato para /login em testMode", () => {
        render(
            <MemoryRouter>
                <NotFound404 testMode={true} />
            </MemoryRouter>
        );
        expect(mockNavigate).toHaveBeenCalledWith("/login");
  
    })
});
