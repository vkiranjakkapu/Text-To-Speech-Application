import "./App.css";
import usePrincipal from "./context/usePrincipal";
import AppRoutes from "./routes/AppRoutes";

function App() {
    const { isLoggedIn } = usePrincipal();

    return (
        <main className="relative h-screen overflow-y-scroll">
            {!isLoggedIn && (
                <div
                    className="absolute inset-0 pointer-events-none opacity-30 dark:opacity-20 bg-cover bg-center bg-no-repeat"
                    style={{ backgroundImage: `url('/bg.png')` }}
                ></div>
            )}
            <AppRoutes />
        </main>
    );
}

export default App;
