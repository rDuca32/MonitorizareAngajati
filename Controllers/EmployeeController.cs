using Microsoft.AspNetCore.Mvc;
using MonitorizareAngajati.Models;
using MonitorizareAngajati.Services;

namespace MonitorizareAngajati.Controllers
{
    public class EmployeeController : Controller
    {
        private readonly EmployeeService _employeeService;

        public EmployeeController(EmployeeService employeeService)
        {
            _employeeService = employeeService;
        }

        // ...existing code...

        public IActionResult Index()
        {
            var employees = _employeeService.GetAll();
            return View(employees);
        }

        public IActionResult Details(int id)
        {
            var employee = _employeeService.GetById(id);
            if (employee == null)
                return NotFound();
            return View(employee);
        }

        [HttpPost]
        public IActionResult Create(Employee employee)
        {
            if (ModelState.IsValid)
            {
                _employeeService.Create(employee);
                return RedirectToAction(nameof(Index));
            }
            return View(employee);
        }

        [HttpPost]
        public IActionResult Edit(Employee employee)
        {
            if (ModelState.IsValid)
            {
                _employeeService.Update(employee);
                return RedirectToAction(nameof(Index));
            }
            return View(employee);
        }

        [HttpPost]
        public IActionResult Delete(int id)
        {
            _employeeService.Delete(id);
            return RedirectToAction(nameof(Index));
        }

        // ...existing code...
    }
}
