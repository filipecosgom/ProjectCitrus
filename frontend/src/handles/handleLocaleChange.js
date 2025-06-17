import useLocaleStore from "../stores/useLocaleStore";

export const handleLocaleChange = (newLocale) => {
    console.log("Changing locale to:", newLocale);
    useLocaleStore.getState().setLocale(newLocale);
}

export default handleLocaleChange;