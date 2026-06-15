import { useEffect, useState } from 'react';
import { useParams } from 'react-router';
import { useDispatch } from 'react-redux';
import { completePacking } from '@stores/packingSlice';

const PackingInvoicePage = () => {
    const { packingInvoiceNumber } = useParams();
    const dispatch = useDispatch();
    const [result, setResult] = useState(null); // { status, message }
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        dispatch(completePacking({ packingInvoiceNumber }))
          .then((res) => {
            setResult({
              status: res.payload?.status,
              message: res.payload?.message
            });
          })
          .finally(() => setLoading(false));
    }, []);

    if (loading) {
        return (
            <div style={{ textAlign: 'center', padding: '2rem' }}>
                <h2 style={{ color: 'green' }}>처리 중입니다...</h2>
            </div>
        );
    };

    return (
        <div style={{ textAlign: 'center', padding: '2rem' }}>
            {result?.status
                ? <h2 style={{ color: 'green' }}>✅ {result.message}</h2>
                : <h2 style={{ color: 'red' }}>❌ {result?.message}</h2>
            }
        </div>
    );
};

export default PackingInvoicePage;