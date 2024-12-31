import React, { useEffect } from 'react';
import { useAuthStore } from '../store/UseAuthStore.js';

const NoChatSelected = () => {
    const { getProfile } = useAuthStore();

    useEffect(() => {
        getProfile();
    }, [getProfile]);

    return (
        <div>NoChatSelected</div>
    );
};

export default NoChatSelected;