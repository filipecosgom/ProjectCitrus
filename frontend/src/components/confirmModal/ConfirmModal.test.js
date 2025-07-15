// Mocks primeiro!
jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key, fallback) => fallback || key,
  }),
}));
jest.mock("react-icons/fa", () => ({
  FaTimes: () => <span data-testid="icon-close" />,
  FaExclamationTriangle: () => <span data-testid="icon-warning" />,
}));

import React from "react";
import {
  render,
  screen,
  fireEvent,
  waitForElementToBeRemoved,
} from "@testing-library/react";
import ConfirmModal from "./ConfirmModal";

describe("ConfirmModal", () => {
  const onClose = jest.fn();
  const onConfirm = jest.fn();

  beforeEach(() => {
    onClose.mockClear();
    onConfirm.mockClear();
  });

  it("renders when open", () => {
    render(
      <ConfirmModal
        isOpen={true}
        onClose={onClose}
        onConfirm={onConfirm}
        title="Test Title"
        message="Test Message"
      />
    );
    expect(screen.getByText("Test Title")).toBeInTheDocument();
    expect(screen.getByText("Test Message")).toBeInTheDocument();
  });

  it("does not render when closed", async () => {
    const { rerender } = render(
      <ConfirmModal
        isOpen={true}
        onClose={onClose}
        onConfirm={onConfirm}
        title="Test Title"
      />
    );
    expect(screen.getByText("Test Title")).toBeInTheDocument();

    rerender(
      <ConfirmModal
        isOpen={false}
        onClose={onClose}
        onConfirm={onConfirm}
        title="Test Title"
      />
    );

    await waitForElementToBeRemoved(() => screen.queryByText("Test Title"));
  });

  it("calls onClose when close button is clicked", () => {
    render(
      <ConfirmModal isOpen={true} onClose={onClose} onConfirm={onConfirm} />
    );
    const closeBtn = screen.getByTestId("icon-close");
    fireEvent.click(closeBtn);
    expect(onClose).toHaveBeenCalled();
  });

  it("calls onConfirm and onClose when confirm button is clicked", () => {
    render(
      <ConfirmModal isOpen={true} onClose={onClose} onConfirm={onConfirm} />
    );
    const confirmBtn = screen
      .getAllByRole("button")
      .find((btn) => btn.className.includes("confirm-modal-button-confirm"));
    fireEvent.click(confirmBtn);
    expect(onConfirm).toHaveBeenCalled();
    expect(onClose).toHaveBeenCalled();
  });

  it("calls onClose when backdrop is clicked", async () => {
    render(
      <ConfirmModal isOpen={true} onClose={onClose} onConfirm={onConfirm} />
    );
    const backdrop = await screen.findByTestId("confirm-modal-backdrop");
    fireEvent.click(backdrop, { target: backdrop, currentTarget: backdrop });
    expect(onClose).toHaveBeenCalled();
  });
});
