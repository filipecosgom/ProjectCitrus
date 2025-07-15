package pt.uc.dei.services;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import pt.uc.dei.repositories.ConfigurationRepository;

@Stateless
public class SettingsService {

    @Inject
    private ConfigurationRepository configurationRepository;

    public boolean setTwoFactorAuthEnabled(boolean enabled) {
        try {
            return configurationRepository.updateTwoFactorAuthEnabled(enabled);
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean getTwoFactorAuthEnabled() {
        try {
            return configurationRepository.getLatestTwoFactorAuthEnabled();
        } catch (Exception e) {
            return null;
        }
    }
}