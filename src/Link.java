
public class Link<T extends Appointment>
{
	public T appointment;
	public Link<T> next;

	public Link(T ap, Link<T> next)
	{
		assert(ap!=null);

		this.appointment = ap;
		this.next = next;
	}
}
