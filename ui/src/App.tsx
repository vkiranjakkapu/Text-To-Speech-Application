import "./App.css";
import NavbarComponent from "./components/Navbar";
import AppRoutes from "./routes/AppRoutes";

function App() {
    return (
        <main className="h-screen">
            <NavbarComponent />
            <AppRoutes />
        </main>
    );
}

export default App;
