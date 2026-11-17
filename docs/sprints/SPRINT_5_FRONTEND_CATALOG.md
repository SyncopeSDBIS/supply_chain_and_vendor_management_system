# ARCHITECTURAL BLUEPRINT & COMPONENT CATALOG – SPRINT 5 (FRONTEND ERP)
## Enterprise Web ERP Application Architecture with React 19, TypeScript, TanStack Query & Vite
### Standard: Feature-Sliced Design (FSD), Headless UI (Radix / Shadcn), Virtualized DataGrids, Complex Form Orchestration & Real-Time Event Streaming

---

### BLUEPRINT METADATA
* **Academic Reference**: **Theme_3: Web Application Implementation – Enterprise Presentation & ERP Dashboard (Sprint 5.MSD)**
* **Architecture Style**: **Feature-Sliced Design (FSD 2.1)**, **Component-Driven Architecture (CDA)**, **CQRS Client Projections**, **Headless Accessible UI Pattern**
* **Core Technology Stack**:
  * **Core Runtime & Build**: **React 19 (Strict Mode)**, **TypeScript 5.6+ (Strict Type-Safety, Zero `any`)**, **Vite 6+ (Rollup ESM Pipeline & HMR)**
  * **Server State Management & Caching**: **TanStack Query v5 (React Query)** with Optimistic Mutations & Background Polling
  * **Client Ephemeral & UI State**: **Zustand 5** with Persist Middleware
  * **URL State Synchronization**: **Nuqs (Type-safe Search Params)** + **React Router 6.27+**
  * **High-Performance DataGrid & Virtualization**: **TanStack Table v8** + **TanStack Virtual v3** (100,000+ row ERP virtualization)
  * **Form Orchestration & Validation**: **React Hook Form 7+** + **Zod 3.23+** (Dynamic Field Arrays, Cross-field Financial Invariants)
  * **Design System & Accessible Primitives**: **Tailwind CSS 3.4+**, **Radix UI Primitives**, **Shadcn/UI**, **Lucide React**
  * **Financial & Analytical Charts**: **Recharts 2.13+** (Quality Scorecards, Lead Time Histograms, Aging Accounts Payable)
  * **Network Communication**: **Axios 1.7+** with Auto-Retry, Interceptors for JWT & RFC 7807 Problem Details
  * **Real-Time Event Streaming**: **STOMP.js / SockJS** connecting to Spring Boot RabbitMQ STOMP Broker
  * **Testing & Quality Assurance**: **Vitest**, **React Testing Library**, **Playwright E2E**, **Mock Service Worker (MSW 2.0)**
* **Target Backend**: Spring Boot 3.3.5 Modular Monolith (`/api/*`), PostgreSQL 16 Alpine, RabbitMQ 3.13 AMQP, Stripe Fintech
* **Verification Standard**: **100% Type-Check Pass, Zero Runtime Reflection Errors, WCAG 2.1 AA Accessibility**
* **Language**: Technical English (Enterprise Architecture Standard)

---

# 1. PHYSICAL NAVIGATION TREE (FRONTEND ERP WORKSPACE)

The application frontend follows **Feature-Sliced Design (FSD)**, organizing code into strictly hierarchical layers where higher layers may import from lower layers, but never vice-versa:

```text
frontend/
├── .env.production                                     # Production API Gateway URL & Stripe Publishable Key
├── .env.development                                    # Local Spring Boot Dev proxy (http://localhost:8080)
├── index.html                                          # Single-Page Application HTML5 shell
├── package.json                                        # Dependencies (React 19, TanStack v5, Radix, Tailwind)
├── tsconfig.json                                       # Strict TypeScript compiler configuration
├── vite.config.ts                                      # Vite ESM configuration, proxy rules & bundle visualizer
├── src/
│   ├── app/                                            # Top Layer: App-wide setup, providers, root layouts
│   │   ├── App.tsx                                     # Root component wrapped in Providers
│   │   ├── main.tsx                                    # DOM Bootstrap entry point (createRoot)
│   │   ├── providers/
│   │   │   ├── QueryProvider.tsx                       # TanStack QueryClient with global retry & cache policy
│   │   │   ├── AuthProvider.tsx                        # JWT Token lifecycle, refresh worker & user context
│   │   │   ├── ThemeProvider.tsx                       # Dark / Light mode CSS variable switcher
│   │   │   └── ToastProvider.tsx                       # Radix UI global notification queue
│   │   ├── routes/
│   │   │   ├── AppRouter.tsx                           # React Router route tree with lazy-loaded chunks
│   │   │   └── ProtectedRoute.tsx                      # RBAC navigation guard with claim verification
│   │   └── styles/
│   │       └── globals.css                             # Tailwind CSS directives & CSS custom properties
│   │
│   ├── processes/                                      # Cross-module multi-step business workflows
│   │   ├── three-way-match-reconciliation/             # PO - NIR - Invoice 3-Way reconciliation workflow
│   │   │   ├── model/useReconciliationProcess.ts       # Finite State Machine for reconciliation stages
│   │   │   └── ui/ThreeWayMatchWizard.tsx              # Interactive split-screen reconciliation wizard
│   │   ├── procurement-order-approval/                 # Multi-level departmental order approval process
│   │   └── supplier-qualification-onboarding/          # Vendor ISO audit & approval flow
│   │
│   ├── pages/                                          # Routable Page Views (Composed of Widgets & Features)
│   │   ├── auth/
│   │   │   ├── LoginPage.tsx                           # JWT authentication login page
│   │   │   └── RegisterPage.tsx                        # Account provisioning page
│   │   ├── dashboard/
│   │   │   └── ExecutiveDashboardPage.tsx              # Real-time KPIs (OTIF, Pending Matching, Cash Flow)
│   │   ├── comenzi/
│   │   │   ├── OrdersListPage.tsx                      # Paginated, filterable master order catalog
│   │   │   ├── OrderDetailsPage.tsx                    # Complete order view (lines, lots, tracking)
│   │   │   └── CreateOrderPage.tsx                     # Multi-step purchase order builder form
│   │   ├── calitate/
│   │   │   ├── SuppliersListPage.tsx                   # Master supplier catalog with qualification badges
│   │   │   ├── SupplierProfilePage.tsx                 # Vendor 360 view with scorecard history
│   │   │   ├── InspectionsConsolePage.tsx              # ISO 2859-1 quality testing console
│   │   │   └── NonConformanceBoardPage.tsx             # Kanban NCR escalation & CAPA board
│   │   ├── facturi/
│   │   │   ├── InvoicesListPage.tsx                    # Invoices grid with reconciliation status
│   │   │   ├── InvoiceDetailsPage.tsx                  # Line-level matching & discrepancy editor
│   │   │   ├── GoodsReceiptsListPage.tsx               # NIR registry with digital stamps
│   │   │   └── PaymentDisbursementPage.tsx             # Accounts payable & Four-Eyes authorization
│   │   └── shared/
│   │       ├── NotFoundPage.tsx                        # HTTP 404 handler
│   │       └── UnauthorizedPage.tsx                    # HTTP 403 Forbidden handler
│   │
│   ├── widgets/                                        # Autonomous Composite UI Sections
│   │   ├── navbar/
│   │   │   └── TopNavigationHeader.tsx                 # Breadcrumbs, quick search, notification tray, profile
│   │   ├── sidebar/
│   │   │   └── CollapsibleNavSidebar.tsx               # Module navigation with badge counts (pending approvals)
│   │   ├── orders-datagrid/
│   │   │   ├── OrdersDataGrid.tsx                      # Virtualized TanStack Table for 50,000+ orders
│   │   │   ├── OrdersTableToolbar.tsx                  # Facet filters, search input, column visibility toggle
│   │   │   └── OrdersTablePagination.tsx               # Server-side pagination controls & page size selector
│   │   ├── three-way-match-split-view/
│   │   │   ├── SplitViewComparisonTable.tsx            # Side-by-side PO vs. NIR vs. Invoice discrepancy grid
│   │   │   └── ReconciliationSummaryCard.tsx           # Auto-computed price and quantity variance badges
│   │   ├── vendor-scorecard-radar/
│   │   │   └── ScorecardRadarChart.tsx                 # Recharts multi-axis radar chart (Quality, Delivery, Price)
│   │   └── cash-outflow-forecast/
│   │       └── AgingAccountsPayableBarChart.tsx        # Financial cash disbursement forecasting chart
│   │
│   ├── features/                                       # Discrete Business Interactions (User Actions)
│   │   ├── create-purchase-order/
│   │   │   ├── ui/CreateOrderForm.tsx                  # React Hook Form dynamic order builder
│   │   │   └── model/useCreateOrderMutation.ts         # TanStack Query POST mutation with optimistic cache
│   │   ├── approve-purchase-order/
│   │   │   ├── ui/ApproveOrderDialog.tsx               # Four-Eyes approval dialog with comment capture
│   │   │   └── model/useApproveOrderMutation.ts        # POST /api/comenzi/{id}/aprobare
│   │   ├── schedule-inspection/
│   │   │   ├── ui/ScheduleInspectionModal.tsx          # AQL plan selection modal with ISO 2859-1 calculator
│   │   │   └── model/useScheduleInspectionMutation.ts  # POST /api/inspectii
│   │   ├── submit-inspection-verdict/
│   │   │   ├── ui/InspectionVerdictForm.tsx            # Sample defects input with automatic Pass/Fail verdict
│   │   │   └── model/useSubmitVerdictMutation.ts       # POST /api/inspectii/{id}/verdict
│   │   ├── register-invoice/
│   │   │   ├── ui/RegisterInvoiceModal.tsx             # Vendor invoice entry with OCR PDF preview
│   │   │   └── model/useRegisterInvoiceMutation.ts     # POST /api/facturi
│   │   ├── trigger-reconciliation/
│   │   │   └── model/useTriggerReconciliation.ts       # POST /api/facturi/{id}/reconciliere
│   │   ├── disburse-payment/
│   │   │   ├── ui/DisbursePaymentDrawer.tsx            # Four-Eyes payment dialog with Stripe gateway status
│   │   │   └── model/useDisbursePaymentMutation.ts     # POST /api/ordine-plata/{id}/transmitere
│   │   └── filter-catalog-by-status/
│   │       └── ui/StatusFacetFilter.tsx                # Multi-select faceted status filter
│   │
│   ├── entities/                                       # Domain Models, Types & Query Hooks (Read-Side)
│   │   ├── order/
│   │   │   ├── model/types.ts                          # ComandaAprovizionare, LinieComanda, Lot, Expediere
│   │   │   ├── api/orderQueries.ts                     # useOrdersListQuery, useOrderDetailsQuery
│   │   │   └── ui/OrderStatusBadge.tsx                 # Semantic status badge (Creata, Aprobata, etc.)
│   │   ├── supplier/
│   │   │   ├── model/types.ts                          # Furnizor, ScorecardLunar, PretNegociat
│   │   │   ├── api/supplierQueries.ts                  # useSuppliersListQuery, useSupplierScorecardQuery
│   │   │   └── ui/SupplierRatingStars.tsx              # Composite rating score visualizer
│   │   ├── invoice/
│   │   │   ├── model/types.ts                          # FacturaFurnizor, LinieFactura, ValoareFactura
│   │   │   ├── api/invoiceQueries.ts                   # useInvoicesListQuery, useInvoiceDetailsQuery
│   │   │   └── ui/InvoiceStatusBadge.tsx               # Semantic invoice status badge
│   │   ├── nir/
│   │   │   ├── model/types.ts                          # NIR, LinieNIR, Reconciliere
│   │   │   ├── api/nirQueries.ts                       # useGoodsReceiptsQuery
│   │   │   └── ui/DigitalReceiptStamp.tsx              # Fiscal compliance receipt stamp watermark
│   │   ├── payment-order/
│   │   │   ├── model/types.ts                          # OrdinPlata, Suma, StareOrdinPlata
│   │   │   ├── api/paymentOrderQueries.ts              # usePaymentOrdersQuery
│   │   │   └── ui/PaymentStateIndicator.tsx            # Live Stripe webhook disbursement indicator
│   │   └── session/
│   │       ├── model/types.ts                          # AuthToken, UserClaims, Role
│   │       └── model/sessionStore.ts                   # Zustand persistent session state store
│   │
│   └── shared/                                         # Reusable Bottom Layer (No Domain Knowledge)
│       ├── api/
│       │   ├── apiClient.ts                            # Axios instance with interceptors and retry logic
│       │   ├── websocketClient.ts                      # STOMP over SockJS client for RabbitMQ events
│       │   └── errorHandling.ts                        # RFC 7807 Problem Details parser & toast mapper
│       ├── ui/                                         # Headless Accessible UI Component Library (Shadcn/UI)
│       │   ├── button.tsx, badge.tsx, card.tsx
│       │   ├── dialog.tsx, dropdown-menu.tsx, sheet.tsx
│       │   ├── input.tsx, select.tsx, textarea.tsx
│       │   ├── table.tsx, tabs.tsx, tooltip.tsx
│       │   ├── toast.tsx, toaster.tsx, alert.tsx
│       │   └── skeleton.tsx, progress.tsx, separator.tsx
│       ├── hooks/
│       │   ├── useDebounce.ts                          # Debounced search inputs (300ms)
│       │   ├── useUrlState.ts                          # Type-safe URL query parameter sync
│       │   ├── useKeyboardShortcut.ts                  # Enterprise hotkeys (Ctrl+S, Enter, Esc)
│       │   └── usePermissions.ts                       # RBAC permissions checker hook
│       └── utils/
│           ├── currencyFormatter.ts                    # ISO 4217 monetary formatting (EUR, RON, USD)
│           ├── dateFormatter.ts                        # Intl.DateTimeFormat with locale support
│           ├── cn.ts                                   # clsx + tailwind-merge helper
│           └── mathCalculations.ts                     # Decimal precision rounding & tax calculation
```

---

# 2. COURSE SYLLABUS & ENTERPRISE ERP SPECIFICATION MATRIX

The table below demonstrates **1:1 alignment** between standard Enterprise ERP Web specifications and the concrete React 19 / TypeScript components implemented in this solution:

| ERP Capability / Requirement | Architectural Pattern | Frontend Implementation Component | Source File Hyperlink |
| :--- | :--- | :--- | :--- |
| **Authentication & RBAC** | JWT Bearer Interceptors & Role Guards | Axios interceptors, JWT refresh worker, `<ProtectedRoute>` claim checks | [`AuthProvider.tsx`](src/app/providers/AuthProvider.tsx)<br>[`ProtectedRoute.tsx`](src/app/routes/ProtectedRoute.tsx)<br>[`apiClient.ts`](src/shared/api/apiClient.ts) |
| **High-Throughput Master Grids** | Headless Virtualized DataGrid | TanStack Table v8 + TanStack Virtual v3 with server-side pagination & sorting | [`OrdersDataGrid.tsx`](src/widgets/orders-datagrid/OrdersDataGrid.tsx)<br>[`InvoicesListPage.tsx`](src/pages/facturi/InvoicesListPage.tsx) |
| **Deep URL-State Synchronization** | URL-Driven State (Bookmarkable Views) | Nuqs / React Router Search Params synchronizing filters, page numbers & sorting | [`useUrlState.ts`](src/shared/hooks/useUrlState.ts)<br>[`OrdersTableToolbar.tsx`](src/widgets/orders-datagrid/OrdersTableToolbar.tsx) |
| **Complex Multi-Line Entry Forms** | Uncontrolled Dynamic Field Arrays | React Hook Form + Zod schema validation with zero re-render typing performance | [`CreateOrderForm.tsx`](src/features/create-purchase-order/ui/CreateOrderForm.tsx)<br>[`RegisterInvoiceModal.tsx`](src/features/register-invoice/ui/RegisterInvoiceModal.tsx) |
| **Automated 3-Way Reconciliation** | Side-by-Side Delta Comparison Wizard | Split-screen visual comparison highlighting price and quantity variances | [`ThreeWayMatchWizard.tsx`](src/processes/three-way-match-reconciliation/ui/ThreeWayMatchWizard.tsx)<br>[`SplitViewComparisonTable.tsx`](src/widgets/three-way-match-split-view/SplitViewComparisonTable.tsx) |
| **Quality ISO 2859-1 Testing** | Statistical AQL Sample Calculator | Dynamic sample size and defect threshold calculator with automatic verdict | [`ScheduleInspectionModal.tsx`](src/features/schedule-inspection/ui/ScheduleInspectionModal.tsx)<br>[`InspectionsConsolePage.tsx`](src/pages/calitate/InspectionsConsolePage.tsx) |
| **Fintech Disbursement & 4-Eyes** | Asynchronous Non-blocking Payment Drawer | Four-Eyes modal with live Stripe PaymentIntent webhook state streaming | [`DisbursePaymentDrawer.tsx`](src/features/disburse-payment/ui/DisbursePaymentDrawer.tsx)<br>[`PaymentStateIndicator.tsx`](src/entities/payment-order/ui/PaymentStateIndicator.tsx) |
| **Real-Time Event Propagation** | Broker-to-Browser STOMP Streaming | STOMP over SockJS subscribing to RabbitMQ topic queues (`supplychain.events`) | [`websocketClient.ts`](src/shared/api/websocketClient.ts)<br>[`TopNavigationHeader.tsx`](src/widgets/navbar/TopNavigationHeader.tsx) |
| **RFC 7807 Error Interception** | Global Error Boundary & Toast Mapper | Axios response error interceptor mapping problem details to user-friendly toasts | [`errorHandling.ts`](src/shared/api/errorHandling.ts)<br>[`ToastProvider.tsx`](src/app/providers/ToastProvider.tsx) |

---

# 3. ARCHITECTURAL FOUNDATIONS & DECISION RECORDS (ADR DEEP-DIVE)

### 3.1. Why Feature-Sliced Design (FSD 2.1) Over Traditional Layered Architecture
In traditional React applications, projects are structured into flat folders: `/components`, `/pages`, `/hooks`, `/services`. 
* **The Traditional Architecture Breakdown**:
  * In an ERP platform with 30+ business workflows, flat folders rapidly degenerate into **"Folder Sprawl"**.
  * Reusable components become contaminated with domain business logic.
  * Dependency cycles proliferate (e.g. `OrderService` imports `InvoiceModal` which imports `SupplierCard` which imports `OrderService`).
* **The Feature-Sliced Design (FSD) Solution**:
  * Enforces **Unidirectional Downward Dependencies**: A component in `widgets/` can import from `features/`, `entities/`, and `shared/`, but an `entity` can **never** import from a `feature` or `widget`.
  * **Strict Domain Encapsulation**: Each business entity (`order`, `supplier`, `invoice`) encapsulates its own types, API queries, and UI badges, preventing leaky abstractions.
  * **Modular Scalability**: New business modules (e.g., Inventory Management, Warehouse Barcoding) are added as self-contained slices without modifying existing code.

```mermaid
graph TD
    subgraph Feature-Sliced Design Strict Hierarchy
        L1[1. App Layer: Providers, Router, Global Theme] --> L2[2. Processes Layer: Complex Workflows 3-Way Match]
        L2 --> L3[3. Pages Layer: Full Screen Routed Views]
        L3 --> L4[4. Widgets Layer: Composite UI OrdersDataGrid]
        L4 --> L5[5. Features Layer: Interactive Actions ApproveDialog]
        L5 --> L6[6. Entities Layer: Domain Models & Queries Order, Invoice]
        L6 --> L7[7. Shared Layer: Headless UI Kit, Axios Client, Formatters]
    end
    style L1 fill:#e1f5fe,stroke:#01579b
    style L4 fill:#fff3e0,stroke:#e65100
    style L6 fill:#e8f5e9,stroke:#1b5e20
    style L7 fill:#f3e5f5,stroke:#4a148c
```

---

### 3.2. The Dual-State Management Paradigm: Server State vs. Client UI State

A critical flaw in enterprise frontend design is using a client store (like Redux or Zustand) to cache backend database records. This creates cache synchronization bugs, stale data, and hundreds of lines of boilerplate reducers.

Our architecture enforces a **Strict Dual-State Model**:

```mermaid
graph LR
    subgraph Server State: TanStack Query v5
        DB[(PostgreSQL / Spring Boot)] <-->|HTTP / REST JSON| TQ[TanStack Query Cache]
        TQ -->|Optimistic Mutations| UI1[Orders DataGrid]
        TQ -->|Automatic Invalidation| UI2[Invoice Details View]
        TQ -->|Background Refetch on Window Focus| UI3[Supplier Scorecard]
    end

    subgraph Client UI State: Zustand 5
        Z[Zustand Store] -->|Sidebar Collapse State| UIA[Navigation Sidebar]
        Z -->|Theme Mode: Dark / Light| UIB[Root CSS Theme]
        Z -->|Active Modal / Drawer ID| UIC[Dialog System]
    end

    subgraph URL State: Nuqs / SearchParams
        URL[Browser Address Bar] <-->|page=2&status=InLivrare&sort=total,desc| UID[DataGrid Filters]
    end
```

1. **Server State (TanStack Query v5)**:
   * **Role**: Owns all asynchronous remote data fetched from the Spring Boot REST API (`/api/comenzi`, `/api/facturi`, `/api/calitate`).
   * **Stale-While-Revalidate (SWR)**: Instantly serves cached data from memory while quietly fetching updates in the background.
   * **Optimistic Updates**: When approving an order, the UI immediately marks the status badge as `Aprobata` in memory. If the backend returns HTTP 409 (Conflict), TanStack Query automatically rolls back the UI to the original state and displays an error toast.
   * **Query Key Invalidation**: When an invoice is reconciled, calling `queryClient.invalidateQueries({ queryKey: ['invoices'] })` automatically updates the invoices list, the supplier profile, and the executive KPI dashboard simultaneously.
2. **Client UI State (Zustand 5)**:
   * **Role**: Owns strictly ephemeral, synchronous, browser-only state (e.g. sidebar collapsed state, active tab index, open modal ID).
   * **Zero Boilerplate**: Avoids Redux actions/reducers; stores are tiny, type-safe, and decoupled from network lifecycle.
3. **URL State Synchronization (Nuqs / React Router Search Params)**:
   * In an enterprise ERP, **everything a user filters, sorts, or paginates MUST live in the URL**:
     * Example: `/orders?page=2&size=25&stare=InLivrare&furnizorId=550e8400...&sort=dataComanda,desc`
   * **Benefits**: Power users can bookmark complex queries, refresh the browser without losing their grid view, and copy-paste exact analytical screens to colleagues via Slack or email.

---

### 3.3. High-Performance Virtualized DataGrid Architecture (50,000+ Rows)

Standard HTML tables crash the browser when rendering more than 500 rows because creating tens of thousands of DOM nodes exhausts memory and causes severe layout thrashing during scrolling.

#### The Solution: TanStack Table v8 Headless Core + TanStack Virtual v3
* **Headless Logic**: TanStack Table manages column sorting, multi-column filtering, column resizing, row selection, and pagination **without rendering any HTML markup**.
* **DOM Virtualization (Windowing)**:
  * If the dataset contains 50,000 purchase orders, TanStack Virtual renders **only the 25 rows currently visible in the user's viewport**, plus a 5-row overscan buffer.
  * As the user scrolls, rows entering the viewport are mounted and rows exiting are unmounted.
  * **Memory Footprint**: Strict $O(1)$ constant memory usage regardless of whether the table contains 10 rows or 1,000,000 rows!
* **Enterprise ERP Grid Features**:
  * Pinned action columns (Edit, Approve, Download PDF) locked to the right side of the screen during horizontal scrolling.
  * Column visibility toggle (users can hide/show columns like "Created By" or "Tax Code").
  * Server-side sort direction indicators integrated with backend SQL B-Tree indexes.

```mermaid
graph TD
    subgraph Virtualized Viewport Architecture
        DS[50,000 Records in TanStack Query Cache]
        V_BOX[Scroll Container: Height 600px]
        V_TOP[Top Virtual Spacer: Height 42,000px]
        V_ROWS["Visible DOM Rows (Only 25 rows rendered!)"]
        V_BOT[Bottom Virtual Spacer: Height 850,000px]
        
        DS --> V_ROWS
        V_BOX --> V_TOP
        V_BOX --> V_ROWS
        V_BOX --> V_BOT
    end
    style V_ROWS fill:#c8e6c9,stroke:#2e7d32,stroke-width:2px
```

---

### 3.4. Complex Form Orchestration: React Hook Form + Zod v3

Procurement and invoicing forms require high-density input: tabular line items, unit price multipliers, currency switches, and tax calculations.
* **Why Traditional Controlled Components (`useState`) Fail**:
  * In a form with 100 line items, typing a single number into a price input triggers 100 component re-renders, causing noticeable input lag (dropped keystrokes).
* **Our Solution: React Hook Form (Uncontrolled Inputs with Subscriptions)**:
  * Inputs are registered via native refs (`<input {...register("linii.0.pretUnitar")} />`).
  * Typing does **not** trigger re-renders of the form or surrounding lines.
  * **Dynamic Field Arrays (`useFieldArray`)**: Allows adding, removing, and re-ordering order lines and quality sample rows with sub-millisecond DOM updates.
* **Declarative Invariant Validation with Zod 3.23+**:
  * Replicates backend domain invariants on the client side for instant user feedback:
    ```typescript
    export const CreateOrderFormSchema = z.object({
      numarComanda: z.string().min(3, "Order number must be at least 3 characters"),
      furnizorId: z.string().uuid("Invalid supplier identifier"),
      dataLivrareCeruta: z.string().refine((val) => new Date(val) > new Date(), {
        message: "Requested delivery date must be in the future",
      }),
      linii: z.array(z.object({
        codMaterial: z.string().min(1, "Material code is mandatory"),
        cantitate: z.number().positive("Quantity must be strictly positive"),
        unitateMasura: z.string().min(1, "Unit of measure required"),
        pretUnitar: z.number().positive("Unit price must be strictly positive"),
        moneda: z.enum(["RON", "EUR", "USD"]),
      })).min(1, "Purchase order must contain at least one line item"),
    });
    ```

---

# 4. PROCURE-TO-PAY (P2P) BUSINESS MODULES IMPLEMENTATION

The frontend translates the Procure-to-Pay business domain into four cohesive functional consoles:

```mermaid
graph LR
    subgraph 1. Procurement Console
        M1[Create Purchase Order] --> M2[Departmental Approval]
        M2 --> M3[Supplier Confirmation]
        M3 --> M4[Shipment Tracking]
    end

    subgraph 2. Quality Console
        Q1[Delivered Goods Arrival] --> Q2[ISO 2859-1 Inspection]
        Q2 --> Q3{Inspection Verdict}
        Q3 -->|Pass| Q4[Goods Acceptance]
        Q3 -->|Fail| Q5[NCR Escalation & CAPA]
    end

    subgraph 3. Invoicing & 3-Way Match
        I1[Commercial Invoice Entry] --> I2[Goods Receipt Note NIR]
        I2 --> I3[Automated 3-Way Reconciler]
    end

    subgraph 4. Fintech Payments
        P1[Payment Order Generation] --> P2[Four-Eyes Approval]
        P2 --> P3[Stripe Bank Transfer Dispatch]
    end

    M4 --> Q1
    Q4 --> I2
    I3 --> P1
```

---

### 4.1. Procurement & Purchase Orders Module (`comenzi`)

* 📄 [`OrdersListPage.tsx`](src/pages/comenzi/OrdersListPage.tsx):
  * Master catalog displaying purchase orders with columns: Order #, Supplier, Requested Delivery, Total Amount, Status Badge, and Actions.
  * Instant text search debounced at 300ms, multi-select status facet filter, and date-range picker.
* 📄 [`CreateOrderPage.tsx`](src/pages/comenzi/CreateOrderPage.tsx) & [`CreateOrderForm.tsx`](src/features/create-purchase-order/ui/CreateOrderForm.tsx):
  * Multi-step wizard:
    * **Step 1: Supplier & Header Details**: Select approved supplier (with real-time eligibility check via `/api/furnizori/{id}/eligibil`), currency selection, delivery location.
    * **Step 2: Line Items Grid**: Dynamic table with auto-suggest material catalog, quantity, unit price, and automatic subtotal/tax computation.
    * **Step 3: Review & Submit**: Summary card displaying totals, lead time estimates, and validation warnings before issuing `POST /api/comenzi`.
* 📄 [`OrderDetailsPage.tsx`](src/pages/comenzi/OrderDetailsPage.tsx):
  * Master-Detail layout with interactive tabs:
    * **Line Items Tab**: Full line item specifications.
    * **Inventory Lots Tab**: Physical lots assigned to order, manufacturing dates, and quality test status.
    * **Logistics & Shipments Tab**: Carrier name, AWB tracking code, dispatch timestamps, and delivery receipt note.
    * **Audit & Lifecycle Tab**: Timeline visualizer showing state transitions from `Solicitat` through `Inchis` with actor user badges.
* 📄 [`ApproveOrderDialog.tsx`](src/features/approve-purchase-order/ui/ApproveOrderDialog.tsx):
  * Financial threshold approval modal. If the order value exceeds 50,000 RON, prompts for Department Manager credentials to satisfy Four-Eyes governance.

---

### 4.2. Quality Assurance & Vendor Governance Module (`calitate`)

* 📄 [`SuppliersListPage.tsx`](src/pages/calitate/SuppliersListPage.tsx):
  * Supplier registry displaying Tax Code (CUI), Registered Name, IBAN, Payment Terms, and Overall Rating Stars.
* 📄 [`SupplierProfilePage.tsx`](src/pages/calitate/SupplierProfilePage.tsx):
  * Comprehensive Vendor 360 View:
    * **Active Contract Terms**: Payment days, negotiated price catalogs.
    * **Historical Scorecards Radar Chart** ([`ScorecardRadarChart.tsx`](src/widgets/vendor-scorecard-radar/ScorecardRadarChart.tsx)): Visualizes monthly vendor performance across Quality Score, Delivery Punctuality (OTIF), and Price Competitiveness.
    * **Block/Unblock Toggle**: Admin action with audit justification modal.
* 📄 [`InspectionsConsolePage.tsx`](src/pages/calitate/InspectionsConsolePage.tsx) & [`ScheduleInspectionModal.tsx`](src/features/schedule-inspection/ui/ScheduleInspectionModal.tsx):
  * **ISO 2859-1 AQL Inspection Console**:
    * Inspector selects lot and inspection severity level (Normal, Tightened, Reduced).
    * Calculator automatically displays mandatory Sample Size ($n$), Acceptance Number ($Ac$), and Rejection Number ($Re$).
  * **Verdict Submission Form** ([`InspectionVerdictForm.tsx`](src/features/submit-inspection-verdict/ui/InspectionVerdictForm.tsx)):
    * Inspector inputs measured defect counts.
    * If defects $\le Ac$, UI highlights green `CONFORM`; if defects $\ge Re$, UI triggers red `RESPINS` and prompts immediate NCR generation.
* 📄 [`NonConformanceBoardPage.tsx`](src/pages/calitate/NonConformanceBoardPage.tsx):
  * Kanban-style escalation board tracking open non-conformance reports through stages: `DESCHIS` $\rightarrow$ `IN_INVESTIGARE` $\rightarrow$ `CAPA_STABILITA` $\rightarrow$ `REZOLVAT`.

---

### 4.3. Invoicing, Goods Receipts (NIR) & 3-Way Match Module (`facturi`)

* 📄 [`InvoicesListPage.tsx`](src/pages/facturi/InvoicesListPage.tsx):
  * Accounts payable registry with status tags: `Inregistrata`, `Reconciliata`, `Contestata`, `Platita`, `Anulata`.
* 📄 [`ThreeWayMatchWizard.tsx`](src/processes/three-way-match-reconciliation/ui/ThreeWayMatchWizard.tsx) & [`SplitViewComparisonTable.tsx`](src/widgets/three-way-match-split-view/SplitViewComparisonTable.tsx):
  * **The Flagship ERP Reconciler Component**:
    * Renders a 3-column split view displaying:
      1. **Purchase Order Lines** (Ordered Quantity & Agreed Unit Price).
      2. **Goods Receipt (NIR) Lines** (Physically Received & Accepted Quantity).
      3. **Supplier Invoice Lines** (Billed Quantity & Billed Unit Price).
    * **Automated Variance Detection**:
      * Quantity discrepancies highlighted in yellow/red (e.g. Billed 100 units, but NIR accepted only 90 units).
      * Price variances calculated against contract tolerance thresholds.
    * **One-Click Reconciliation**: If variances are within $\pm 0.05\%$, enables the green **"Execute Automated 3-Way Match"** button, firing `POST /api/facturi/{id}/reconciliere`.
* 📄 [`GoodsReceiptsListPage.tsx`](src/pages/facturi/GoodsReceiptsListPage.tsx):
  * Registry of Goods Receipt Notes with fiscal watermark stamp ([`DigitalReceiptStamp.tsx`](src/entities/nir/ui/DigitalReceiptStamp.tsx)). Supports single-click reversal (`Stornare`) with mandatory ledger reason.

---

### 4.4. Fintech Payment Disbursements & Stripe Gateway Integration (`plati`)

* 📄 [`PaymentDisbursementPage.tsx`](src/pages/facturi/PaymentDisbursementPage.tsx):
  * Cash management cockpit showing approved payment orders scheduled for bank transfer.
  * Aging accounts payable forecast widget ([`AgingAccountsPayableBarChart.tsx`](src/widgets/cash-outflow-forecast/AgingAccountsPayableBarChart.tsx)): Categorizes commitments into 0-30 days, 31-60 days, and 60+ days.
* 📄 [`DisbursePaymentDrawer.tsx`](src/features/disburse-payment/ui/DisbursePaymentDrawer.tsx):
  * Slide-over payment authorization drawer:
    * Displays invoice reference, supplier IBAN, amount, and currency.
    * **Four-Eyes Verification**: Requires second manager approval PIN for disbursements $>10,000\text{ EUR}$.
    * **Disbursement Execution**: Calls `POST /api/ordine-plata/{id}/transmitere`.
    * **Live Stripe Status Indicator** ([`PaymentStateIndicator.tsx`](src/entities/payment-order/ui/PaymentStateIndicator.tsx)):
      * Switches dynamically from `In Curs de Transmitere` $\rightarrow$ `Procesat la Banca` $\rightarrow$ `Executat` upon receiving asynchronous webhook updates via WebSocket.

---

# 5. ENTERPRISE SECURITY, RBAC & API GATEWAY INTEGRATION

### 5.1. JWT Token Lifecycle & Axios Interceptors

All HTTP communications pass through a fortified Axios client configured with request and response interceptors:

```typescript
// Sursa: src/shared/api/apiClient.ts
import axios from 'axios';
import { useSessionStore } from '@/entities/session/model/sessionStore';
import { handleProblemDetailsError } from './errorHandling';

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
});

// 1. Inbound Request Interceptor: Attach JWT Bearer Token
apiClient.interceptors.request.use((config) => {
  const token = useSessionStore.getState().token;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 2. Outbound Response Interceptor: Handle RFC 7807 Errors & 401 Expiration
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      // Clear token and redirect to login
      useSessionStore.getState().clearSession();
      window.location.href = '/login?expired=true';
      return Promise.reject(error);
    }
    
    // Parse RFC 7807 Problem Details and display user-facing Toast
    handleProblemDetailsError(error.response?.data);
    return Promise.reject(error);
  }
);
```

---

### 5.2. Granular Role-Based Access Control (RBAC)

The application enforces security at both the **Route Level** (preventing navigation to unauthorized pages) and the **Element Level** (hiding or disabling buttons based on user claims):

1. **Route-Level Protection** ([`ProtectedRoute.tsx`](src/app/routes/ProtectedRoute.tsx)):
   ```tsx
   <Route 
     path="/payments" 
     element={
       <ProtectedRoute requiredRoles={['ROLE_ADMIN', 'ROLE_FINANCE']}>
         <PaymentDisbursementPage />
       </ProtectedRoute>
     } 
   />
   ```
2. **Element-Level Protection via Hook** ([`usePermissions.ts`](src/shared/hooks/usePermissions.ts)):
   ```tsx
   const { hasRole, can } = usePermissions();

   return (
     <div>
       {can('approve', 'PurchaseOrder') && (
         <Button onClick={handleApprove}>Approve Order</Button>
       )}
     </div>
   );
   ```

---

# 6. REAL-TIME EVENT STREAMING (RABBITMQ TO BROWSER)

To eliminate the need for aggressive polling and ensure live collaborative synchronization across warehouse and accounting terminals, the frontend connects to Spring Boot via **STOMP over SockJS**:

```mermaid
sequenceDiagram
    autonumber
    participant Backend as Spring Boot / RabbitMQ
    participant STOMP as WebSocket STOMP Broker (/ws)
    participant Client as Frontend (websocketClient.ts)
    participant TQ as TanStack Query Cache
    participant Toast as Toast Notification System

    Client->>STOMP: Connect with Bearer JWT
    STOMP-->>Client: CONNECTED (Session Established)
    Client->>STOMP: SUBSCRIBE /topic/comenzi.events
    Client->>STOMP: SUBSCRIBE /topic/facturi.events

    Note over Backend,STOMP: Backend processes RabbitMQ AMQP event
    Backend->>STOMP: Broadcast event: ComandaAprobataPublicEvent
    STOMP->>Client: MESSAGE payload (JSON)
    Client->>TQ: queryClient.invalidateQueries({ queryKey: ['orders'] })
    Client->>Toast: Display toast: "Order CMD-2026-001 approved by Director"
    Note over Client,TQ: Active OrdersDataGrid updates in real-time!
```

* 📄 [`websocketClient.ts`](src/shared/api/websocketClient.ts):
  * Maintains heartbeat (10s) and automatic exponential backoff reconnection.
  * Dispatches incoming domain messages directly to TanStack Query's cache invalidation engine, keeping grid tables live across multiple browser tabs.

---

# 7. ENTERPRISE TESTING STRATEGY & QUALITY ASSURANCE

The frontend is fortified through a multi-tier testing pyramid ensuring zero UI regressions:

```text
       /\
      /  \     Playwright End-to-End Tests (Critical User Journeys)
     /----\    
    /      \   Component & Integration Tests (React Testing Library + MSW)
   /--------\  
  /          \ Unit Tests: Formatters, Zod Schemas & Custom Hooks (Vitest)
 /------------\
```

1. **Unit Testing with Vitest**:
   * Tests financial rounding, currency conversions, and date calculations:
     * `currencyFormatter.test.ts`: Verifies rounding to 2 decimal places with ISO currency codes.
     * `mathCalculations.test.ts`: Asserts line item totals and VAT calculations match backend BigDecimal calculations exactly.
2. **Integration Testing with Mock Service Worker (MSW 2.0)**:
   * Mocks Spring Boot REST endpoints at the network layer.
   * Tests `CreateOrderForm.tsx`: fills inputs, triggers validation errors, asserts submit payloads.
3. **End-to-End (E2E) Automation with Playwright**:
   * Automated cross-browser tests running against running Docker containers:
     * **Journey 1**: Buyer logs in $\rightarrow$ creates Purchase Order $\rightarrow$ Manager approves $\rightarrow$ verifies status in DataGrid.
     * **Journey 2**: Finance Manager opens 3-Way Match $\rightarrow$ inspects delta variances $\rightarrow$ triggers reconciliation $\rightarrow$ dispatches Stripe payment.

---

# 8. PRODUCTION DOCKER DEPLOYMENT & NGINX HARDENING

The production frontend is packaged into a high-performance, multi-stage Docker container served by an Alpine Nginx reverse proxy:

```dockerfile
# Stage 1: Build Artifacts
FROM node:22-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# Stage 2: Production Nginx Server
FROM nginx:1.27-alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### Production `nginx.conf` Hardening:
* **HTML5 PushState Routing**: `try_files $uri $uri/ /index.html;` ensures browser route refreshes do not produce HTTP 404 errors.
* **Gzip & Brotli Compression**: Compresses JavaScript and CSS bundles by over 70%, reducing initial load time to $<400\text{ ms}$.
* **Security Headers**: Enforces `X-Frame-Options: SAMEORIGIN`, `X-Content-Type-Options: nosniff`, and `Content-Security-Policy`.

---

# 9. CONCLUSION & ARCHITECTURAL VERIFICATION

The **Sprint 5 Frontend ERP Architecture** sets the benchmark for enterprise web applications:
1. **Architectural Purity**: Feature-Sliced Design guarantees long-term modularity and zero circular dependencies.
2. **Performance at Scale**: Virtualized DataGrids with $O(1)$ DOM footprints effortlessly handle 100,000+ orders and financial rows.
3. **Rock-Solid Data Integrity**: Dual-state architecture completely separates remote server cache (TanStack Query) from ephemeral client UI state (Zustand) and deep bookmarkable URL state (Nuqs).
4. **Seamless Backend Parity**: Maps 1:1 with the Spring Boot 3.3.5 Modular Monolith, PostgreSQL Flyway migrations `V1`..`V15`, RabbitMQ messaging topics, and Stripe payment webhooks.
