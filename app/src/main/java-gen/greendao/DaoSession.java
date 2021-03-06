package greendao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.ksfc.newfarmer.beans.dbbeans.InviteeEntity;
import com.ksfc.newfarmer.beans.dbbeans.OfflineShoppingCart;
import com.ksfc.newfarmer.beans.dbbeans.PotentialCustomersEntity;

import greendao.InviteeEntityDao;
import greendao.OfflineShoppingCartDao;
import greendao.PotentialCustomersEntityDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig inviteeEntityDaoConfig;
    private final DaoConfig offlineShoppingCartDaoConfig;
    private final DaoConfig potentialCustomersEntityDaoConfig;

    private final InviteeEntityDao inviteeEntityDao;
    private final OfflineShoppingCartDao offlineShoppingCartDao;
    private final PotentialCustomersEntityDao potentialCustomersEntityDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        inviteeEntityDaoConfig = daoConfigMap.get(InviteeEntityDao.class).clone();
        inviteeEntityDaoConfig.initIdentityScope(type);

        offlineShoppingCartDaoConfig = daoConfigMap.get(OfflineShoppingCartDao.class).clone();
        offlineShoppingCartDaoConfig.initIdentityScope(type);

        potentialCustomersEntityDaoConfig = daoConfigMap.get(PotentialCustomersEntityDao.class).clone();
        potentialCustomersEntityDaoConfig.initIdentityScope(type);

        inviteeEntityDao = new InviteeEntityDao(inviteeEntityDaoConfig, this);
        offlineShoppingCartDao = new OfflineShoppingCartDao(offlineShoppingCartDaoConfig, this);
        potentialCustomersEntityDao = new PotentialCustomersEntityDao(potentialCustomersEntityDaoConfig, this);

        registerDao(InviteeEntity.class, inviteeEntityDao);
        registerDao(OfflineShoppingCart.class, offlineShoppingCartDao);
        registerDao(PotentialCustomersEntity.class, potentialCustomersEntityDao);
    }
    
    public void clear() {
        inviteeEntityDaoConfig.getIdentityScope().clear();
        offlineShoppingCartDaoConfig.getIdentityScope().clear();
        potentialCustomersEntityDaoConfig.getIdentityScope().clear();
    }

    public InviteeEntityDao getInviteeEntityDao() {
        return inviteeEntityDao;
    }

    public OfflineShoppingCartDao getOfflineShoppingCartDao() {
        return offlineShoppingCartDao;
    }

    public PotentialCustomersEntityDao getPotentialCustomersEntityDao() {
        return potentialCustomersEntityDao;
    }

}
