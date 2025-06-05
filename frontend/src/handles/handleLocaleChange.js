import useLocaleStore from "../stores/useLocaleStore";

export const handleLocaleChange = (newLocale) => {
    useLocaleStore.getState().setLocale(newLocale);
}

export default handleLocaleChange;