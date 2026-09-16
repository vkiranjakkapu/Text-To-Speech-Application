import "./App.css";
import NavbarComponent from "./components/Navbar";
import AppRoutes from "./routes/AppRoutes";

function App() {
    return (
        <main className="h-screen overflow-hidden">
            <NavbarComponent />
            <AppRoutes />
        </main>
    );
}

export default App;
